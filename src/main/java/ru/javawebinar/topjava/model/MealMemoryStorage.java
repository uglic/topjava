package ru.javawebinar.topjava.model;

import java.util.List;

public class MealMemoryStorage implements IStorage<Meal, Integer> {
    private final static Class THIS_CLASS = Meal.class;
    private MemoryStorage storage;

    public MealMemoryStorage() {
        storage = MemoryStorage.get();
    }

    @Override
    public Integer add(Meal item) {
        return storage.add(item, THIS_CLASS);
    }

    @Override
    public void update(Meal item) {
        storage.update(item, THIS_CLASS);
    }

    @Override
    public void delete(Integer key) {
        storage.delete(key, THIS_CLASS);
    }

    @Override
    public Meal get(Integer key) {
        return storage.get(key, THIS_CLASS);
    }

    @Override
    public long size() {
        return storage.size(THIS_CLASS);
    }

    @Override
    public List<Meal> getAll() {
        return storage.getAll(THIS_CLASS);
    }

    @Override
    public void deleteAll() {
        storage.deleteAll(THIS_CLASS);
    }
}
