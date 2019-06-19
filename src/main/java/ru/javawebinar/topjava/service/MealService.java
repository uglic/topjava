package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.util.List;

public interface MealService {
    Meal create(int userId, Meal meal);

    void delete(int userId, int id);

    Meal get(int userId, int id);

    int update(int userId, Meal meal);

    List<Meal> getAll(int userId);

    List<Meal> getByDateBetween(int userId, LocalDate startDate, LocalDate endDate);
}