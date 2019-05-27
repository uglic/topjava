package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> mealList = Arrays.asList(
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин", 510)
        );
        List<UserMealWithExceed> result;

        result = getFilteredWithExceeded(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        System.out.println("\nCycles 'forEach' version result:");
        result.forEach(System.out::println);

        result = getFilteredWithExceededStream(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        System.out.println("\nStream version result:");
        result.forEach(System.out::println);

        result = getFilteredWithExceededSingleFor(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        System.out.println("\nSingle 'forEach' pass version result:");
        result.forEach(System.out::println);
    }

    public static List<UserMealWithExceed> getFilteredWithExceeded(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> dateCalories = new HashMap<>(); // to guarantee O(1) access time set capacity >= 1.5*mealList.size() here
        for (UserMeal m : mealList) {
            LocalDate date = m.getDateTime().toLocalDate();
            dateCalories.put(date, dateCalories.getOrDefault(date, 0) + m.getCalories());
        }
        List<UserMealWithExceed> result = new ArrayList<>();
        for (UserMeal m : mealList) {
            LocalTime time = m.getDateTime().toLocalTime();
            if (!time.isBefore(startTime) && !time.isAfter(endTime)) {
                result.add(new UserMealWithExceed(
                        m.getDateTime(),
                        m.getDescription(),
                        m.getCalories(),
                        dateCalories.get(m.getDateTime().toLocalDate()) > caloriesPerDay
                ));
            }
        }
        return result;
    }

    public static List<UserMealWithExceed> getFilteredWithExceededStream(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> dateCount = mealList.stream()
                .collect(
                        Collectors.groupingBy(
                                k -> k.getDateTime().toLocalDate(),
                                Collectors.summingInt(UserMeal::getCalories)
                        )
                );
        return mealList.stream()
                .filter(m -> !m.getDateTime().toLocalTime().isBefore(startTime))
                .filter(m -> !m.getDateTime().toLocalTime().isAfter(endTime))
                .map(m -> new UserMealWithExceed(
                        m.getDateTime(),
                        m.getDescription(),
                        m.getCalories(),
                        (dateCount.get(m.getDateTime().toLocalDate()) > caloriesPerDay)))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExceed> getFilteredWithExceededSingleFor(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> dateCalories = new HashMap<>();
        List<UserMeal> preResult = new ArrayList<>();
        for (UserMeal m : mealList) {
            LocalDate date = m.getDateTime().toLocalDate();
            dateCalories.put(date, dateCalories.getOrDefault(date, 0) + m.getCalories());
            LocalTime time = m.getDateTime().toLocalTime();
            if (!time.isBefore(startTime) && !time.isAfter(endTime)) {
                preResult.add(m);
            }
        }
        List<UserMealWithExceed> result = new ArrayList<>();
        for (UserMeal m : preResult) {
            result.add(new UserMealWithExceed(
                    m.getDateTime(),
                    m.getDescription(),
                    m.getCalories(),
                    dateCalories.get(m.getDateTime().toLocalDate()) > caloriesPerDay
            ));
        }
        return result;
    }
}
