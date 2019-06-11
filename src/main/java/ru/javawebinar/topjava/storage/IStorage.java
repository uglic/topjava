package ru.javawebinar.topjava.storage;

import java.util.List;

public interface IStorage<T, K> {
    K add(T item);

    void update(T item);

    void delete(K key);

    T get(K key);

    List<T> getAll();
}
