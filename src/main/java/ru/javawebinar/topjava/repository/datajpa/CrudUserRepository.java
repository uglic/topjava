package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.User;

import java.util.Optional;

@Transactional(readOnly = true)
public interface CrudUserRepository extends JpaRepository<User, Integer> {
    User getByEmail(String email);

    @EntityGraph(attributePaths = {"id"})
    @Query("SELECT u FROM User u WHERE u.id=:id")
    Optional<User> getByIdFieldsOnly(@Param("id") int id);

    //    https://stackoverflow.com/a/46013654/548473
    @EntityGraph(attributePaths = {"meals"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT u FROM User u WHERE u.id=?1")
    User getWithMeals(int id);
}
