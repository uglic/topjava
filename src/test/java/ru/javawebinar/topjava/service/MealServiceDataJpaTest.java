package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.ActiveDbProfileResolver;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.USER;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ActiveProfiles(resolver = ActiveDbProfileResolver.class)
public class MealServiceDataJpaTest extends AbstractMealServiceTest {
    @Test
    public void getUserWithMeals() {
        Map<User, List<Meal>> userWithMeals = getService().getUserWithMeals(USER_ID);
        User userPersisted = userWithMeals.keySet().iterator().next();
        List<Meal> mealsPersisted = userWithMeals.get(userPersisted);

        assertThat(1).isEqualTo(userWithMeals.size());
        assertThat(USER).isEqualToIgnoringGivenFields(userPersisted, "registered", "roles");
        assertThat(MEALS).usingElementComparatorIgnoringFields("user").isEqualTo(mealsPersisted);
    }

    @Test
    public void getWithUser(){
        Map<Meal, User> mealWithUser = getService().getWithUser(MEAL1_ID, USER_ID);
        Meal mealPersisted = mealWithUser.keySet().iterator().next();
        User userPersisted = mealWithUser.get(mealPersisted);

        assertThat(1).isEqualTo(mealWithUser.size());
        assertThat(MEAL1).isEqualToIgnoringGivenFields(mealPersisted, "user");
        assertThat(USER).isEqualToIgnoringGivenFields(userPersisted, "registered", "roles");
    }
}