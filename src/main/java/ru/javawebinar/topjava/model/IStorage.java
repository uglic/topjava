package ru.javawebinar.topjava.model;

import java.util.List;

public interface IStorage<T, K> {
    K add(T item);

    void update(T item);

    void delete(K key);

    T get(K key);

    long size();

    List<T> getAll();

    void deleteAll();
}
