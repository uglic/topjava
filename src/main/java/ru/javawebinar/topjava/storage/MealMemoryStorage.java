package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.Meal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MealMemoryStorage implements IStorage<Meal, Integer> {
    private final static Map<Integer, Meal> storage = new ConcurrentHashMap<>();
    private final static AtomicInteger counter = new AtomicInteger(0);

    @Override
    public Integer add(Meal item) {
        int id = counter.incrementAndGet();
        storage.put(id, new Meal(id, item.getDateTime(), item.getDescription(), item.getCalories()));
        return id;
    }

    @Override
    public void update(Meal item) {
        storage.computeIfPresent(item.getId(), (k, v) -> item);
    }

    @Override
    public void delete(Integer key) {
        storage.remove(key);
    }

    @Override
    public Meal get(Integer key) {
        return storage.get(key);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(storage.values());
    }
}
