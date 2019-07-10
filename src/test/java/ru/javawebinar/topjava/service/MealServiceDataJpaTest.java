package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.ActiveDbProfileResolver;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.MealTestData.MEALS;
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
}