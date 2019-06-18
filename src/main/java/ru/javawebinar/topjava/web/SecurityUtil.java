package ru.javawebinar.topjava.web;

import static ru.javawebinar.topjava.util.MealsUtil.DEFAULT_CALORIES_PER_DAY;

public class SecurityUtil {
    private static int mythicalUser = 1;

    public static int authUserId() {
        return mythicalUser;
    }

    public static void setAuthUserId(int userId) {
        mythicalUser = userId;
    }

    public static int authUserCaloriesPerDay() {
        return DEFAULT_CALORIES_PER_DAY;
    }
}