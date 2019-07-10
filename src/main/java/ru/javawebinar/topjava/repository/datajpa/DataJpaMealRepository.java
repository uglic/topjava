package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DataJpaMealRepository implements MealRepository {
    private CrudMealRepository crudRepository;
    private CrudUserRepository userRepository;

    public DataJpaMealRepository(CrudMealRepository crudMealRepository, CrudUserRepository userRepository) {
        this.crudRepository = crudMealRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal != null) {
            // !meal.isNew() && ( (mealId = meal.getId()) == null || (persisted = get(mealId, userId)) == null )
            if (!meal.isNew()) {
                Integer mealId = meal.getId();
                if (mealId != null) {
                    Meal persisted = get(mealId, userId);
                    if (persisted != null) {
                        meal.setUser(persisted.getUser());
                        return crudRepository.save(meal);
                    }
                }
            } else {
                meal.setUser(userRepository.getOne(userId));
                return crudRepository.save(meal);
            }
        }
        return null;
    }

    @Override
    public boolean delete(int id, int userId) {
        return crudRepository.delete(id, userId) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        return crudRepository.getByIdAndUserId(id, userId).orElse(null);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return crudRepository.getByUserIdOrderByDateTimeDesc(userId);
    }

    @Override
    public List<Meal> getBetween(LocalDateTime startDate, LocalDateTime endDate, int userId) {
        return crudRepository.getByDateTimeBetweenAndUserIdOrderByDateTimeDesc(
                startDate, endDate, userId);
    }
}
