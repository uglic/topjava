package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static ru.javawebinar.topjava.MealTestData.*;

@ContextConfiguration({"classpath:spring/spring-app.xml", "classpath:spring/spring-db.xml"})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {
    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal meal = service.get(USER_MEAL_1_ID, USER.getId());
        assertMatch(meal, USER_MEAL_1);
    }

    @Test(expected = NotFoundException.class)
    public void getNotExisted() {
        service.get(NOT_EXISTED_MEAL_ID, USER.getId());
    }

    @Test(expected = NotFoundException.class)
    public void getNotOwner() {
        service.get(USER_MEAL_1_ID, ADMIN.getId());
    }

    @Test
    public void delete() {
        service.delete(USER_MEAL_1_ID, USER.getId());
        assertMatch(service.getAll(USER.getId()), USER_MEAL_3, USER_MEAL_2);
    }

    @Test(expected = NotFoundException.class)
    public void deleteNotExisted() {
        service.delete(NOT_EXISTED_MEAL_ID, USER.getId());
    }

    @Test(expected = NotFoundException.class)
    public void deleteNotOwner() {
        service.delete(USER_MEAL_1_ID, ADMIN.getId());
    }

    @Test
    public void getBetweenDates() {
        List<Meal> actual = service.getBetweenDates(USER_MEAL_1.getDate(), USER_MEAL_1.getDate(), USER.getId());
        assertMatch(actual, USER_MEAL_2, USER_MEAL_1);
    }

    @Test
    public void getBetweenDatesEmpty() {
        List<Meal> actual = service.getBetweenDates(null, null, USER.getId());
        assertMatch(actual, USER_MEAL_3, USER_MEAL_2, USER_MEAL_1);
    }

    @Test
    public void getBetweenDatesLeft() {
        List<Meal> actual = service.getBetweenDates(USER_MEAL_3.getDate(), null, USER.getId());
        assertMatch(actual, USER_MEAL_3);
    }

    @Test
    public void getBetweenDatesRight() {
        List<Meal> actual = service.getBetweenDates(null, USER_MEAL_1.getDate(), USER.getId());
        assertMatch(actual, USER_MEAL_2, USER_MEAL_1);
    }

    @Test
    public void getBetweenDateTimes() {
        List<Meal> actual = service.getBetweenDateTimes(
                USER_MEAL_1.getDateTime(), USER_MEAL_2.getDateTime(), USER.getId());
        assertMatch(actual, USER_MEAL_2, USER_MEAL_1);
    }

    @Test
    public void getBetweenDateTimesEmpty() {
        List<Meal> actual = service.getBetweenDateTimes(null, null, USER.getId());
        assertMatch(actual, USER_MEAL_3, USER_MEAL_2, USER_MEAL_1);
    }

    @Test
    public void getBetweenDateTimesLeft() {
        List<Meal> actual = service.getBetweenDateTimes(USER_MEAL_2.getDateTime(), null, USER.getId());
        assertMatch(actual, USER_MEAL_3, USER_MEAL_2);
    }

    @Test
    public void getBetweenDateTimesRight() {
        List<Meal> actual = service.getBetweenDateTimes(null, USER_MEAL_2.getDateTime(), USER.getId());
        assertMatch(actual, USER_MEAL_2, USER_MEAL_1);
    }

    @Test
    public void getAllUser() {
        assertMatch(service.getAll(USER.getId()), USER_MEAL_3, USER_MEAL_2, USER_MEAL_1);
    }

    @Test
    public void getAllAdmin() {
        assertMatch(service.getAll(ADMIN.getId()), ADMIN_MEAL_3, ADMIN_MEAL_2, ADMIN_MEAL_1);
    }

    @Test
    public void update() {
        Meal updated = new Meal(USER_MEAL_1, USER_MEAL_1.getId());
        updated.setDateTime(LocalDateTime.now());
        updated.setDescription("Обновленное");
        updated.setCalories(2134);
        service.update(updated, USER.getId());
        assertMatch(service.get(USER_MEAL_1_ID, USER.getId()), updated);
    }

    @Test(expected = NotFoundException.class)
    public void updateNotExist() {
        Meal updated = new Meal(NEW_MEAL, NOT_EXISTED_MEAL_ID);
        updated.setDateTime(LocalDateTime.now());
        updated.setDescription("Отсутствует");
        updated.setCalories(325);
        service.update(updated, USER.getId());
        //assertMatch(service.get(USER_MEAL_1_ID, USER.getId()), updated);
    }

    @Test(expected = NotFoundException.class)
    public void updateNotOwner() {
        Meal updated = new Meal(USER_MEAL_1, USER_MEAL_1.getId());
        updated.setDateTime(LocalDateTime.now());
        updated.setDescription("Отсутствует");
        updated.setCalories(325);
        service.update(updated, ADMIN.getId()); // while roles are not used
    }

    @Test
    public void create() {
        Meal newMeal = new Meal(NEW_MEAL, null);
        Meal created = service.create(newMeal, USER.getId());
        newMeal.setId(created.getId());
        assertMatch(created, newMeal);
    }
}