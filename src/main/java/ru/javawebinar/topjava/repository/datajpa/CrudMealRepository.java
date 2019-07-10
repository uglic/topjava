package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;

import java.time.LocalDateTime;
import java.util.*;

@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public interface CrudMealRepository extends JpaRepository<Meal, Integer> {
    @Transactional
    @Modifying
    @Query("DELETE FROM Meal m WHERE m.id=:id and m.user.id=:userId")
    int delete(@Param("id") int id, @Param("userId") int userId);

    Optional<Meal> getByIdAndUserId(int id, int userId);

    List<Meal> getByDateTimeBetweenAndUserIdOrderByDateTimeDesc(
            LocalDateTime startDate, LocalDateTime endDate, int userId);

    List<Meal> getByUserIdOrderByDateTimeDesc(int userId);
}
