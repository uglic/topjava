package ru.javawebinar.topjava.data;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MealData {
    private final List<Meal> meals;

    private static class MealDataHolder {
        private static final MealData INSTANCE = new MealData();
    }

    public static List<Meal> getData() {
        return MealDataHolder.INSTANCE.meals;
    }

    private MealData() {
        List<Meal> mealsPrepare = new ArrayList<>();
        String[] names = {"Хлеб", "Чай", "Масло", "Рыба", "Макароны", "Суп", "Курица", "Сыр", "Сок"};
        LocalDate day = LocalDate.of(2019, 6, 2);
        mealsPrepare.add(new Meal(day.atTime(LocalTime.of(9, 10)), names[2], 95));
        mealsPrepare.add(new Meal(day.atTime(LocalTime.of(10, 22)), names[1], 105));
        mealsPrepare.add(new Meal(day.atTime(LocalTime.of(11, 31)), names[3], 120));
        mealsPrepare.add(new Meal(day.atTime(LocalTime.of(12, 44)), names[3], 85));
        mealsPrepare.add(new Meal(day.atTime(LocalTime.of(13, 51)), names[4], 110));
        mealsPrepare.add(new Meal(day.atTime(LocalTime.of(15, 6)), names[5], 125));
        mealsPrepare.add(new Meal(day.atTime(LocalTime.of(16, 12)), names[6], 300));
        mealsPrepare.add(new Meal(day.atTime(LocalTime.of(17, 19)), names[7], 250));
        mealsPrepare.add(new Meal(day.atTime(LocalTime.of(18, 36)), names[6], 150));
        mealsPrepare.add(new Meal(day.atTime(LocalTime.of(19, 42)), names[0], 340));
        mealsPrepare.add(new Meal(day.atTime(LocalTime.of(20, 55)), names[1], 160));
        day = day.plusDays(1);
        mealsPrepare.add(new Meal(day.atTime(LocalTime.of(7, 15)), names[8], 110));
        mealsPrepare.add(new Meal(day.atTime(LocalTime.of(8, 26)), names[1], 180));
        mealsPrepare.add(new Meal(day.atTime(LocalTime.of(9, 38)), names[6], 190));
        mealsPrepare.add(new Meal(day.atTime(LocalTime.of(10, 50)), names[3], 320));
        mealsPrepare.add(new Meal(day.atTime(LocalTime.of(11, 58)), names[5], 100));
        mealsPrepare.add(new Meal(day.atTime(LocalTime.of(13, 11)), names[5], 140));
        mealsPrepare.add(new Meal(day.atTime(LocalTime.of(14, 20)), names[6], 190));
        mealsPrepare.add(new Meal(day.atTime(LocalTime.of(15, 33)), names[2], 100));
        mealsPrepare.add(new Meal(day.atTime(LocalTime.of(16, 46)), names[8], 115));
        mealsPrepare.add(new Meal(day.atTime(LocalTime.of(17, 56)), names[1], 200));
        mealsPrepare.add(new Meal(day.atTime(LocalTime.of(18, 59)), names[7], 150));
        meals = Collections.unmodifiableList(mealsPrepare);
    }
}
