package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepositoryImpl implements MealRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepositoryImpl.class);
    private Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.MEALS1.forEach(m -> save(1, m));
        MealsUtil.MEALS2.forEach(m -> save(2, m));
    }

    @Override
    public Meal save(int userId, Meal meal) {
        log.info("save {}", meal);
        if (meal == null) {
            return null;
        }
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meal.setUserId(userId);
            Map<Integer, Meal> userMeals = repository.computeIfAbsent(userId, v -> new ConcurrentHashMap<>());
            userMeals.put(meal.getId(), meal);
            return meal;
        }
        Meal existingMeal = get(userId, meal.getId());
        if (existingMeal != null) {
            Map<Integer, Meal> userMeals = repository.get(userId);
            if (userMeals != null) {
                meal.setUserId(userId);
                return userMeals.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
            }
        }
        return null;
    }

    @Override
    public boolean delete(int userId, int id) {
        log.info("delete {}", id);
        Map<Integer, Meal> userMeals = repository.get(userId);
        if (userMeals != null && userMeals.remove(id) != null) {
            if (userMeals.size() == 0) {
                repository.remove(userId);
            }
            return true;
        }
        return false;
    }

    @Override
    public Meal get(int userId, int id) {
        log.info("get {}", id);
        Map<Integer, Meal> userMeals = repository.get(userId);
        if (userMeals != null) {
            return userMeals.get(id);
        }
        return null;
    }

    @Override
    public List<Meal> getAll(final int userId) {
        log.info("getAll");
        return getByFilter(userId, f -> true);
    }

    @Override
    public List<Meal> getBetweenStartDateAndEndDate(int userId, LocalDate startDate, LocalDate endDate) {
        log.info("getByDateFilter");
        return getByFilter(userId, f -> DateTimeUtil.isBetween(f.getDate(), startDate, endDate));
    }

    private List<Meal> getByFilter(int userId, Predicate<Meal> filter) {
        Map<Integer, Meal> userMeals = repository.get(userId);
        if (userMeals != null) {
            return userMeals.values().stream()
                    .filter(filter)
                    .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }
}
