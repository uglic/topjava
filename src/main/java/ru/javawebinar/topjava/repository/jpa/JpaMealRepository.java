package ru.javawebinar.topjava.repository.jpa;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class JpaMealRepository implements MealRepository {

    @PersistenceContext
    public EntityManager em;

    @Override
    @Transactional
    public Meal save(Meal meal, int userId) {
        User user;
        if (null != (user = em.getReference(User.class, userId))) {
            meal.setUser(user);
            if (meal.isNew()) {
                em.persist(meal);
                return meal;
            } else {
                Meal persistedMeal = em.find(Meal.class, meal.getId());
                if (persistedMeal != null) {
                    user = persistedMeal.getUser();
                    if (user != null && user.getId() == userId) {
                        return em.merge(meal);
                    }
                }
            }
        }
        return null;
    }

    @Override
    @Transactional
    public boolean delete(int id, int userId) {
        return em.createNamedQuery(Meal.DELETE)
                .setParameter("id", id)
                .setParameter("userId", userId)
                .executeUpdate() > 0;
    }

    @Override
    public Meal get(int id, int userId) {
        Object[] meals = em.createNamedQuery(Meal.GET, Meal.class)
                .setParameter("id", id)
                .setParameter("userId", userId)
                .getResultStream()
                .limit(2)
                .toArray();
        return (meals.length == 1) ? (Meal) meals[0] : null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return em.createNamedQuery(Meal.ALL_SORTED, Meal.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public List<Meal> getBetween(LocalDateTime startDate, LocalDateTime endDate, int userId) {
        return em.createNamedQuery(Meal.BETWEEN_SORTED, Meal.class)
                .setParameter("userId", userId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }
}