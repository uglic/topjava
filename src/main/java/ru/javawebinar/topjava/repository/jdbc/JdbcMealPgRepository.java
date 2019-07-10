package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@Profile("postgres")
public class JdbcMealPgRepository extends AbstractJdbcMealRepository {
    @Autowired
    public JdbcMealPgRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, namedParameterJdbcTemplate);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected LocalDateTime getDateValue(LocalDateTime dateTime) {
        return dateTime;
    }
}
