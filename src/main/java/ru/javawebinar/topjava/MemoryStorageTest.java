package ru.javawebinar.topjava;

import ru.javawebinar.topjava.data.MealData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.storage.MealMemoryStorage;

import java.util.Comparator;
import java.util.List;

public class MemoryStorageTest {
    private final static int TASK_COUNT = 10000;

    public static void main(String[] args) {
        multiThreadTest();
    }

    private static void multiThreadTest() {
        try {
            System.out.format("%s%d\n", "Start  milliseconds: ", System.nanoTime() / 1_000_000);
            Thread[] tasks = new Thread[TASK_COUNT];
            for (int i = 0; i < TASK_COUNT; i++) {
                final int ID = i;
                tasks[i] = new Thread(() -> {
                    MealMemoryStorage storage = new MealMemoryStorage();
                    storage.add(MealData.getData().get(ID % MealData.getData().size()));
                });
            }
            for (Thread task : tasks) {
                task.start();
            }
            for (Thread task : tasks) {
                task.join();
            }
            List<Meal> resultList = new MealMemoryStorage().getAll();
            resultList.sort(Comparator.comparing(Meal::getId));
            System.out.format("%s%d\n", "Finish milliseconds: ", System.nanoTime() / 1_000_000);
            System.out.format("%s%d\n", "Max counter: ", resultList.stream().mapToInt(Meal::getId).max().orElse(0));
            System.out.format("%s%d\n", "Total count: ", resultList.size());


        } catch (InterruptedException e) {
            System.out.println("multiThreadTest is interrupted: " + e.getLocalizedMessage());
        }
    }
}
