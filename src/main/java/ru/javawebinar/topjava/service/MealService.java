package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;

public interface MealService {
    Meal create(int userId, Meal meal);

    void delete(int userId, int id);

    Meal get(int userId, int id);

    void update(int userId, Meal meal);

    Collection<Meal> getAll(int userId);

    Collection<Meal> getByDateBetweenAndTimeBetween(int userId,
                                                    LocalDate startDate, LocalDate endDate,
                                                    LocalTime startTime, LocalTime endTime);
}