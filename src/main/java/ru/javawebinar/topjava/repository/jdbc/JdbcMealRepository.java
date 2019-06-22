package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static ru.javawebinar.topjava.util.DateTimeUtil.*;

@Repository
public class JdbcMealRepository implements MealRepository {
    private static final BeanPropertyRowMapper<Meal> ROW_MAPPER = BeanPropertyRowMapper.newInstance(Meal.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertTool;

    @Autowired
    public JdbcMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertTool = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("meals")
                .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Meal save(Meal meal, int userId) {
        // meal.dateTime must always be the local time in DEFAULT_TIMEZONE,
        // so storing date as timestampZ preserves instant (localtime + tz, approx.)
        Timestamp dateTimeTimestampZ = getTimestampForZone(meal.getDateTime(), DEFAULT_TIMEZONE);
        MapSqlParameterSource fieldMap = new MapSqlParameterSource()
                .addValue("id", meal.getId())
                .addValue("date_time", dateTimeTimestampZ)
                .addValue("description", meal.getDescription())
                .addValue("calories", meal.getCalories())
                .addValue("user_id", userId);
        if (meal.isNew()) {
            Number newKey = insertTool.executeAndReturnKey(fieldMap);
            meal.setId(newKey.intValue());
        } else {
            String query = "UPDATE meals SET date_time=:date_time, description=:description, calories=:calories, user_id=:user_id WHERE id=:id AND user_id=:user_id";
            if (namedParameterJdbcTemplate.update(query, fieldMap) == 0) {
                return null;
            }
        }
        return meal;
    }

    @Override
    public boolean delete(int id, int userId) {
        return jdbcTemplate.update("DELETE FROM meals WHERE id=? AND user_id=?", id, userId) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        // meal.dateTime will always be the local time in DEFAULT_TIMEZONE
        return DataAccessUtils.singleResult(
                jdbcTemplate.query("SELECT * FROM meals WHERE id=? AND user_id=?", ROW_MAPPER, id, userId));
    }

    @Override
    public List<Meal> getAll(int userId) {
        // meal.dateTime will always be the local time in DEFAULT_TIMEZONE
        return jdbcTemplate.query("SELECT * FROM meals WHERE user_id=? ORDER BY date_time DESC, id DESC", ROW_MAPPER, userId);
    }

    @Override
    public List<Meal> getBetween(LocalDateTime startDate, LocalDateTime endDate, int userId) {
        // meal.dateTime will always be the local time in DEFAULT_TIMEZONE
        return jdbcTemplate.query("SELECT * FROM meals WHERE (user_id=?) AND (date_time between ? and ?) ORDER BY date_time DESC, id DESC",
                ROW_MAPPER, userId,
                adjustStartDateTimeToSqlTimestamp(startDate),
                adjustEndDateTimeToSqlTimestamp(endDate));
    }
}
