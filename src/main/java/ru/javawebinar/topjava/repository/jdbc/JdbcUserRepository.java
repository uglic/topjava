package ru.javawebinar.topjava.repository.jdbc;

import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    private final int FIELD_NAME_MIN_LENGTH = 2;
    private final int FIELD_NAME_MAX_LENGTH = 100;
    private final int FIELD_EMAIL_MAX_LENGTH = 100;
    private final int FIELD_PASSWORD_MIN_LENGTH = 5;
    private final int FIELD_PASSWORD_MAX_LENGTH = 100;
    private final int FIELD_CALORIES_MIN = 10;
    private final int FIELD_CALORIES_MAX = 10000;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        checkConstraints(user);
        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
        } else if (0 == namedParameterJdbcTemplate.update(
                "UPDATE users SET name=:name, email=:email, password=:password, " +
                        "registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id",
                parameterSource)) {
            return null;
        } else {
            deleteRoles(user); // better delete+insert only difference but not here
        }
        insertRoles(user);
        return user;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE id=?", ROW_MAPPER, id);
        final User user = DataAccessUtils.singleResult(users);
        if (user != null) {
            user.setRoles(getRolesForUserId(id));
        }
        return user;
    }

    @Override
    public User getByEmail(String email) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        User user = DataAccessUtils.singleResult(users);
        if (user != null) {
            user.setRoles(getRolesForUserId(user.getId()));
        }
        return user;
    }

    @Override
    public List<User> getAll() {
        List<User> users = jdbcTemplate.query("SELECT * FROM users ORDER BY name, email", ROW_MAPPER);
        setRoles(users);
        return users;
    }

    private void insertRoles(User user) {
        int[] affected = jdbcTemplate.batchUpdate("INSERT INTO user_roles(user_id, role) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    private final int userId = user.getId();
                    private int counter = 0;

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        if (counter != i) {
                            throw new IllegalArgumentException("Single direction list");
                        }
                        counter++;
                        Role role = user.getRoles().iterator().next();
                        ps.setInt(1, userId);
                        ps.setString(2, role.name());
                    }

                    @Override
                    public int getBatchSize() {
                        return user.getRoles().size();
                    }
                });
        for (int value : affected) {
            if (value != 1) {
                throw new IllegalArgumentException("Not all roles added");
            }
        }
    }

    private void deleteRoles(User user) {
        jdbcTemplate.update("DELETE FROM user_roles WHERE user_id=?", user.getId());
    }

    private List<Role> getRolesForUserId(int id) {
        return jdbcTemplate.queryForList("SELECT role FROM user_roles WHERE user_id=?",
                new Integer[]{id}, Role.class);
    }

    private void setRoles(List<User> users) {
        final Map<Integer, Collection<Role>> userRolesMap = new HashMap<>();
        jdbcTemplate.queryForList("SELECT * FROM user_roles ORDER BY user_id")
                .forEach(
                        ur -> userRolesMap.computeIfAbsent((Integer) ur.get("user_id"), k -> new HashSet<>())
                                .add(Role.valueOf((String) ur.get("role"))));
        users.forEach(u -> u.setRoles(userRolesMap.get(u.getId())));
    }

    private ConstraintViolation<User> getSimpleViolation(String message, Object value) {
        return ConstraintViolationImpl.forParameterValidation(
                message,
                null, null, null,
                null, null, null, value,
                null, null, null, null,
                null);
    }

    private void checkConstraints(User user) {
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        fillViolations(user.getName(), "{username}", FIELD_NAME_MIN_LENGTH, FIELD_NAME_MAX_LENGTH, violations);
        fillViolations(user.getEmail(), "{email}", null, FIELD_EMAIL_MAX_LENGTH, violations);
        fillViolations(user.getPassword(), "{password}", FIELD_PASSWORD_MIN_LENGTH, FIELD_PASSWORD_MAX_LENGTH, violations);
        fillViolations(user.getCaloriesPerDay(), "{caloriesPerDay}", FIELD_CALORIES_MIN, FIELD_CALORIES_MAX, violations);

        if (violations.size() > 0) {
            throw new ConstraintViolationException(violations);
        }
    }

    private void fillViolations(String value, String field, Integer minValue, Integer maxValue, Set<ConstraintViolation<?>> violations) {
        if (value == null || value.isBlank()) {
            violations.add(getSimpleViolation(field + " must not be blank", value));
        } else if (minValue != null && value.length() < minValue) {
            violations.add(getSimpleViolation(field + " length must be not less then " + minValue, value));
        } else if (maxValue != null && value.length() > maxValue) {
            violations.add(getSimpleViolation(field + " length must be not greater then " + maxValue, value));
        }
    }

    private void fillViolations(int value, String field, Integer minValue, Integer maxValue, Set<ConstraintViolation<?>> violations) {
        if (minValue != null && value < minValue) {
            violations.add(getSimpleViolation(field + " must be not less then " + minValue, value));
        } else if (maxValue != null && value > maxValue) {
            violations.add(getSimpleViolation(field + " must be not greater then " + maxValue, value));
        }
    }
}
