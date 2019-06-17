package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepositoryImpl implements MealRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepositoryImpl.class);
    private Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.MEALS.forEach(m -> save(m.getUserId(), m));
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
            repository.put(meal.getId(), meal);
            return meal;
        }
        Meal existingMeal = get(userId, meal.getId());
        if (existingMeal == null || existingMeal.getUserId() != userId) {
            return null;
        } else {
            meal.setUserId(userId);
            return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
        }
    }

    @Override
    public boolean delete(int userId, int id) {
        log.info("delete {}", id);
        Meal meal = get(userId, id);
        return meal != null && repository.remove(id) != null;
    }

    @Override
    public Meal get(int userId, int id) {
        log.info("get {}", id);
        Meal meal = repository.get(id);
        return (meal != null && meal.getUserId() == userId) ? meal : null;
    }

    @Override
    public Collection<Meal> getAll(final int userId) {
        log.info("getAll");
        return repository.values().stream()
                .filter(m -> m.getUserId() == userId)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Meal> getByDateBetweenAndTimeBetween(int userId,
                                                           LocalDate startDate, LocalDate endDate,
                                                           LocalTime startTime, LocalTime endTime) {
        log.info("getByDateTimeBetween {}", userId);
        return repository.values().stream()
                .filter(m -> m.getUserId() == userId)
                .filter(m -> {
                    LocalDate d = m.getDate();
                    return d.compareTo(startDate) >= 0 && d.compareTo(endDate) <= 0;
                })
                .filter(m -> {
                    LocalTime t = m.getTime();
                    return t.compareTo(startTime) >= 0 && t.compareTo(endTime) <= 0;
                })
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}
