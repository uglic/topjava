package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DataJpaMealRepository implements MealRepository {
    private static final Sort SORT = new Sort(Sort.Direction.DESC, "date_time");


    @Autowired
    private CrudMealRepository crudRepository;

    @Override
    public Meal save(Meal meal, int userId) {
        if (!meal.isNew()) {
            Integer mealId = meal.getId();
            if (mealId == null || get(mealId, userId) == null) {
                return null;
            }
        }
        return crudRepository.save(meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        return crudRepository.delete(id, userId) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        return crudRepository.findById(id)
                .filter(m -> {
                    User user = m.getUser();
                    if (user != null) {
                        Integer uid = user.getId();
                        return uid != null && uid == userId;
                    }
                    return false;
                })
                .orElse(null);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return crudRepository.findAll(SORT);
    }

    @Override
    public List<Meal> getBetween(LocalDateTime startDate, LocalDateTime endDate, int userId) {
        return crudRepository.getByDateTimeBetweenAndUserIdOrderByDateTimeDesc(
                startDate, endDate, userId);
    }
}
