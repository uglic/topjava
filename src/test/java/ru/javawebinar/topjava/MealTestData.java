package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.UserTestData.*;

public class MealTestData {
    public static final User USER = UserTestData.USER;
    public static final User ADMIN = UserTestData.ADMIN;
    public static final int USER_MEAL_1_ID = ADMIN_ID + 1;
    public static final int USER_MEAL_2_ID = ADMIN_ID + 2;
    public static final int USER_MEAL_3_ID = ADMIN_ID + 3;
    public static final int ADMIN_MEAL_1_ID = ADMIN_ID + 4;
    public static final int ADMIN_MEAL_2_ID = ADMIN_ID + 5;
    public static final int ADMIN_MEAL_3_ID = ADMIN_ID + 6;

    public static final int NOT_EXISTED_MEAL_ID = ADMIN_ID + 1000;

    // Must be in (dateTime, Id) order per each user
    public static final Meal USER_MEAL_1 = new Meal(USER_MEAL_1_ID, LocalDateTime.of(2019, 6, 20, 7, 50), "Клиент откушал раз", 100);
    public static final Meal USER_MEAL_2 = new Meal(USER_MEAL_2_ID, LocalDateTime.of(2019, 6, 20, 9, 10), "Клиент откушал два", 210);
    public static final Meal USER_MEAL_3 = new Meal(USER_MEAL_3_ID, LocalDateTime.of(2019, 6, 21, 11, 20), "Клиент откушал три", 320);
    public static final Meal ADMIN_MEAL_1 = new Meal(ADMIN_MEAL_1_ID, LocalDateTime.of(2019, 6, 20, 11, 5), "Админ ел как один", 120);
    public static final Meal ADMIN_MEAL_2 = new Meal(ADMIN_MEAL_2_ID, LocalDateTime.of(2019, 6, 20, 15, 10), "Админ ел за двоих", 230);
    public static final Meal ADMIN_MEAL_3 = new Meal(ADMIN_MEAL_3_ID, LocalDateTime.of(2019, 6, 21, 7, 50), "Админ ел за троих", 360);
    public static final Meal NEW_MEAL = new Meal(LocalDateTime.of(2019, 6, 21, 11, 52), "Клиент еще не ел", 10);

    public static void assertMatch(Meal actual, Meal expected) {
        //assertThat(actual).isEqualToIgnoringGivenFields(expected );
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        //assertThat(actual).usingElementComparatorIgnoringFields().isEqualTo(expected);
        assertThat(actual).usingFieldByFieldElementComparator().isEqualTo(expected);
    }
}
