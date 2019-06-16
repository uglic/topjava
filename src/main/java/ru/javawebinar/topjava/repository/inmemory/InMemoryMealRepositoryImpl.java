package ru.javawebinar.topjava.repository.inmemory;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemoryMealRepositoryImpl implements MealRepository {
    private Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.MEALS.forEach(this::save);
    }

    @Override
    public Meal save(Meal meal) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meal.setUserId(SecurityUtil.authUserId());
            repository.put(meal.getId(), meal);
            return meal;
        }
        Meal existingMeal = get(meal.getId());
        if (isAllowAccess(meal) && isAllowAccess(existingMeal)) {
            return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
        } else {
            return null;
        }
    }

    @Override
    public boolean delete(int id) {
        Meal meal = get(id);
        return isAllowAccess(meal) && repository.remove(id) != null;
    }

    @Override
    public Meal get(int id) {
        Meal meal = repository.get(id);
        return isAllowAccess(meal) ? meal : null;
    }

    @Override
    public Collection<Meal> getAll() {
        final int userId = SecurityUtil.authUserId();
        return repository.values().stream()
                .filter(m -> m.getUserId() == userId)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    private boolean isAllowAccess(Meal meal) {
        return (meal != null && meal.getUserId() == SecurityUtil.authUserId());
    }
}
