package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

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

        result = getFilteredWithExceededStream2(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        System.out.println("\nStream2 version result:");
        result.forEach(System.out::println);

        result = getFilteredWithExceededStream3(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        System.out.println("\nStream3 (final) version result:");
        result.forEach(System.out::println);
    }

    public static List<UserMealWithExceed> getFilteredWithExceeded(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> dateCalories = new HashMap<>(); // to guarantee O(1) access time set capacity >= 1.5*mealList.size() here
        mealList.forEach(m -> {
            LocalDate date = m.getDateTime().toLocalDate();
            dateCalories.put(date, dateCalories.getOrDefault(date, 0) + m.getCalories());
        });
        List<UserMealWithExceed> result = new ArrayList<>();
        mealList.forEach(m -> {
            if (TimeUtil.isBetween(m.getDateTime().toLocalTime(), startTime, endTime)) {
                result.add(new UserMealWithExceed(
                        m.getDateTime(),
                        m.getDescription(),
                        m.getCalories(),
                        dateCalories.get(m.getDateTime().toLocalDate()) > caloriesPerDay
                ));
            }
        });
        return result;
    }

    public static List<UserMealWithExceed> getFilteredWithExceededStream(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> dateCount = mealList.stream()
                .collect(
                        groupingBy(
                                k -> k.getDateTime().toLocalDate(),
                                summingInt(UserMeal::getCalories)
                        )
                );
        return mealList.stream()
                .filter(m -> TimeUtil.isBetween(m.getDateTime().toLocalTime(), startTime, endTime))
                .map(m -> new UserMealWithExceed(
                        m.getDateTime(),
                        m.getDescription(),
                        m.getCalories(),
                        (dateCount.get(m.getDateTime().toLocalDate()) > caloriesPerDay)))
                .collect(toList());
    }

    public static List<UserMealWithExceed> getFilteredWithExceededStream2(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return mealList
                .stream()
                .collect(groupingBy(k -> k.getDateTime().toLocalDate(), toList()))
                .entrySet()
                .stream()
                .collect(
                        groupingBy(
                                k -> k.getValue().stream().mapToInt(UserMeal::getCalories).sum() > caloriesPerDay,
                                mapping(Map.Entry::getValue,
                                        collectingAndThen(toList(),
                                                p -> p.stream().flatMap(Collection::stream).collect(toList())
                                        ))))
                .entrySet()
                .stream()
                .flatMap(m -> m.getValue()
                        .stream()
                        .filter(f -> TimeUtil.isBetween(f.getDateTime().toLocalTime(), startTime, endTime))
                        .map(v -> new UserMealWithExceed(
                                v.getDateTime(),
                                v.getDescription(),
                                v.getCalories(),
                                m.getKey()
                        )))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExceed> getFilteredWithExceededStream3(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return mealList
                .stream()
                .collect(groupingBy(k -> k.getDateTime().toLocalDate(), toList()))
                .values()
                .stream()
                .flatMap(p -> p.stream()
                        .filter(f -> TimeUtil.isBetween(f.getDateTime().toLocalTime(), startTime, endTime))
                        .map(v -> new UserMealWithExceed(
                                v.getDateTime(),
                                v.getDescription(),
                                v.getCalories(),
                                p.stream()
                                        .mapToInt(UserMeal::getCalories)
                                        .sum() > caloriesPerDay)
                        ))
                .collect(Collectors.toList());
    }
}
