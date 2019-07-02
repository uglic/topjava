package ru.javawebinar.topjava.service;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.TransactionSystemException;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(MealServiceTest.class);
    private static List<TestLogRecord> totalLog = new ArrayList<>();

    @Autowired
    private MealService service;

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Rule
    public Stopwatch stopwatch = new Stopwatch() {
        @Override
        protected void finished(long nanos, Description description) {
            TestLogRecord record = new TestLogRecord();
            record.testName = description.getMethodName();
            record.timeMillis = TimeUnit.NANOSECONDS.toMillis(nanos);
            record.log();
            totalLog.add(record);
        }
    };

    private static class TestLogRecord {
        String testName;
        Long timeMillis;

        void log() {
            MDC.put("testName", testName);
            MDC.put("timeMillis", String.format("%d", timeMillis));
            logger.info(new StringBuilder() // if MDC class is not used
                    .append("Test [")
                    .append(testName)
                    .append("] finished in ")
                    .append(timeMillis)
                    .append(" milliseconds")
                    .toString());
            MDC.remove("testName");
            MDC.remove("timeMillis");
        }
    }

    @BeforeClass
    public static void setup() {
        totalLog.clear();
    }

    @AfterClass
    public static void finish() {
        logger.info("\nResult of tests:");
        totalLog.forEach(TestLogRecord::log);
    }

    @Test
    public void delete() throws Exception {
        service.delete(MEAL1_ID, USER_ID);
        assertMatch(service.getAll(USER_ID), MEAL6, MEAL5, MEAL4, MEAL3, MEAL2);
    }

    @Test
    public void deleteNotFound() throws Exception {
        thrown.expect(NotFoundException.class);
        service.delete(1, USER_ID);
    }

    @Test
    public void deleteNotOwn() throws Exception {
        thrown.expect(NotFoundException.class);
        service.delete(MEAL1_ID, ADMIN_ID);
    }

    @Test
    public void create() throws Exception {
        Meal newMeal = getCreated();
        Meal created = service.create(newMeal, USER_ID);
        newMeal.setId(created.getId());
        assertMatch(newMeal, created);
        assertMatch(service.getAll(USER_ID), newMeal, MEAL6, MEAL5, MEAL4, MEAL3, MEAL2, MEAL1);
    }

    @Test
    public void get() throws Exception {
        Meal actual = service.get(ADMIN_MEAL_ID, ADMIN_ID);
        assertMatch(actual, ADMIN_MEAL1);
    }

    @Test
    public void getNotFound() throws Exception {
        thrown.expect(NotFoundException.class);
        service.get(1, USER_ID);
    }

    @Test
    public void getNotOwn() throws Exception {
        thrown.expect(NotFoundException.class);
        service.get(MEAL1_ID, ADMIN_ID);
    }

    @Test
    public void update() throws Exception {
        Meal updated = getUpdated();
        service.update(updated, USER_ID);
        assertMatch(service.get(MEAL1_ID, USER_ID), updated);
    }

    @Test
    public void updateNotFound() throws Exception {
        thrown.expect(NotFoundException.class);
        service.update(MEAL1, ADMIN_ID);
    }

    @Test
    public void getAll() throws Exception {
        assertMatch(service.getAll(USER_ID), MEALS);
    }

    @Test
    public void getBetween() throws Exception {
        assertMatch(service.getBetweenDates(
                LocalDate.of(2015, Month.MAY, 30),
                LocalDate.of(2015, Month.MAY, 30), USER_ID), MEAL3, MEAL2, MEAL1);
    }

    @Test
    public void createDescriptionNull() {
        thrown.expect(TransactionSystemException.class);
        service.create(getNewMeal(-1, Meal.MIN_CALORIES_VALUE + 1), USER_ID);
    }

    @Test
    public void createDescriptionBlank() {
        thrown.expect(TransactionSystemException.class);
        service.create(getNewMeal(Meal.MIN_DESCRIPTION_LEN, ' ', Meal.MIN_CALORIES_VALUE + 1), USER_ID);
    }

    @Test
    public void createDescriptionShort() {
        thrown.expect(TransactionSystemException.class);
        service.create(getNewMeal(Meal.MIN_DESCRIPTION_LEN - 1, Meal.MIN_CALORIES_VALUE), USER_ID);
    }

    @Test
    public void createDescriptionLong() {
        thrown.expect(TransactionSystemException.class);
        service.create(getNewMeal(Meal.MAX_DESCRIPTION_LEN + 1, Meal.MIN_CALORIES_VALUE), USER_ID);
    }

    @Test
    public void createCaloriesLow() {
        thrown.expect(TransactionSystemException.class);
        service.create(getNewMeal(Meal.MIN_DESCRIPTION_LEN, Meal.MIN_CALORIES_VALUE - 1), USER_ID);
    }

    @Test
    public void createCaloriesHigh() {
        thrown.expect(TransactionSystemException.class);
        service.create(getNewMeal(Meal.MIN_DESCRIPTION_LEN, Meal.MAX_CALORIES_VALUE + 1), USER_ID);
    }

    @Test
    public void updateDescriptionNull() {
        thrown.expect(TransactionSystemException.class);
        Meal meal = getMealForUpdate(MEAL1);
        meal.setDescription(getStringOfLength(-1, 'z'));
        service.update(meal, USER_ID);
    }

    @Test
    public void updateDescriptionBlank() {
        thrown.expect(TransactionSystemException.class);
        Meal meal = getMealForUpdate(MEAL1);
        meal.setDescription(getStringOfLength(Meal.MIN_DESCRIPTION_LEN, ' '));
        service.update(meal, USER_ID);
    }

    @Test
    public void updateDescriptionShort() {
        thrown.expect(TransactionSystemException.class);
        Meal meal = getMealForUpdate(MEAL1);
        meal.setDescription(getStringOfLength(Meal.MIN_DESCRIPTION_LEN - 1, 'z'));
        service.update(meal, USER_ID);
    }

    @Test
    public void updateDescriptionLong() {
        thrown.expect(TransactionSystemException.class);
        Meal meal = getMealForUpdate(MEAL1);
        meal.setDescription(getStringOfLength(Meal.MAX_DESCRIPTION_LEN + 1, 'z'));
        service.update(meal, USER_ID);
    }

    @Test
    public void updateCaloriesLow() {
        thrown.expect(TransactionSystemException.class);
        Meal meal = getMealForUpdate(MEAL1);
        meal.setCalories(Meal.MIN_CALORIES_VALUE - 1);
        service.update(meal, USER_ID);
    }

    @Test
    public void updateCaloriesHigh() {
        thrown.expect(TransactionSystemException.class);
        Meal meal = getMealForUpdate(MEAL1);
        meal.setCalories(Meal.MAX_CALORIES_VALUE + 1);
        service.update(meal, USER_ID);
    }
}