package ru.javawebinar.topjava.model;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryStorage {
    private final Map<String, Map<Integer, ? extends AbstractEntity>> storage = new ConcurrentHashMap<>();
    private final Map<String, Number> counter = new ConcurrentHashMap<>();

    private MemoryStorage() {
    }

    private final static class MemoryStorageHelper {
        private final static MemoryStorage memoryStorage = new MemoryStorage();
    }

    public static MemoryStorage get() {
        return MemoryStorageHelper.memoryStorage;
    }

    public <T extends AbstractEntity> Integer add(T item, Class itemClass) {
        Map<Integer, T> classStorage = getObjectStorage(itemClass);
        AtomicInteger classCounter = getObjectCounter(itemClass);
        int id = classCounter.incrementAndGet();
        classStorage.put(id, cloneWithNewId(item, id, itemClass));
        //classStorage.put(id, (T)item.cloneWithNewId(id));
        return id;
    }

    public <T extends AbstractEntity> void update(T item, Class itemClass) {
        Map<Integer, T> classStorage = getObjectStorage(itemClass);
        if (classStorage.computeIfPresent(item.getId(), (k, v) -> item) == null) {
            throw new IllegalArgumentException("Key is absent: " + item.getId() + " [" + itemClass.getCanonicalName() + "]");
        }
    }

    public <T extends AbstractEntity> void delete(Integer key, Class itemClass) {
        Map<Integer, T> classStorage = getObjectStorage(itemClass);
        classStorage.remove(key);
    }

    public <T extends AbstractEntity> T get(Integer key, Class itemClass) {
        Map<Integer, T> classStorage = getObjectStorage(itemClass);
        return classStorage.get(key);
    }

    public <T extends AbstractEntity> long size(Class itemClass) {
        Map<Integer, T> classStorage = getObjectStorage(itemClass);
        return classStorage.size();
    }

    public <T extends AbstractEntity> List<T> getAll(Class itemClass) {
        Map<Integer, T> classStorage = getObjectStorage(itemClass);
        return new ArrayList<>(classStorage.values());
    }

    public <T extends AbstractEntity> void deleteAll(Class itemClass) {
        Map<Integer, T> classStorage = getObjectStorage(itemClass);
        classStorage.clear();
    }

    @SuppressWarnings("unchecked")
    private <T extends AbstractEntity> Map<Integer, T> getObjectStorage(Class clazz) {
        String className = clazz.getCanonicalName();
        return (Map<Integer, T>) storage.computeIfAbsent(className, c -> new ConcurrentHashMap<Integer, T>());
    }

    private AtomicInteger getObjectCounter(Class clazz) {
        String className = clazz.getCanonicalName();
        return (AtomicInteger) counter.computeIfAbsent(className, c -> new AtomicInteger(0));
    }

    @SuppressWarnings("unchecked")
    private <T extends AbstractEntity> T cloneWithNewId(T item, Integer newId, Class clazz) {
        try {
            Field[] fieldsAll = clazz.getDeclaredFields();
            List<Field> fields = new ArrayList<>();
            for (Field field : fieldsAll) {
                if (Modifier.isPrivate(field.getModifiers())) {
                    fields.add(field);
                }
            }
            Class<?>[] arguments = new Class<?>[fields.size() + 1];
            Object[] factParameters = new Object[fields.size() + 1];
            arguments[0] = int.class;
            factParameters[0] = newId;
            for (int i = 0; i < fields.size(); i++) {
                arguments[i + 1] = fields.get(i).getType();
                String getName = fields.get(i).getName();
                getName = "get" + getName.substring(0, 1).toUpperCase() + getName.substring(1);
                factParameters[i + 1] = item.getClass().getMethod(getName).invoke(item);
            }
            return (T) clazz.getConstructor(arguments).newInstance(factParameters);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }
}
