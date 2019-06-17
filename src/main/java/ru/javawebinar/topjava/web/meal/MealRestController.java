package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;

import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
public class MealRestController {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private MealService service;

    @Autowired
    public MealRestController(MealService service) {
        this.service = service;
    }

    public Meal create(Meal meal) {
        log.info("create {}", meal);
        return service.create(authUserId(), meal);
    }

    public void delete(int id) {
        log.info("delete {}", id);
        service.delete(authUserId(), id);
    }


    public Meal get(int id) {
        log.info("get {}", id);
        return service.get(authUserId(), id);
    }


    public void update(Meal meal) {
        log.info("update {} ", meal);
        service.update(authUserId(), meal);
    }


    public Collection<Meal> getAll() {
        log.info("getAll");
        return service.getAll(authUserId());
    }

    public Collection<Meal> getByDateBetweenAndTimeBetween(int userId, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        log.info("getByDateBetweenAndTimeBetween");
        return service.getByDateBetweenAndTimeBetween(userId, startDate, endDate, startTime, endTime);
    }
}