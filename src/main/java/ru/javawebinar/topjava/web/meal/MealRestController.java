package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNotNew;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserCaloriesPerDay;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
public class MealRestController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private MealService service;

    @Autowired
    public MealRestController(MealService service) {
        this.service = service;
    }

    public Meal create(Meal meal) {
        log.info("create {}", meal);
        checkNew(meal);
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


    public int update(Meal meal) {
        log.info("update {} ", meal);
        checkNotNew(meal);
        service.update(authUserId(), meal);
        return service.get(authUserId(), meal.getId()).getId();
    }

    public List<MealTo> getAll() {
        log.info("getAll");
        return MealsUtil.getFilteredWithExcess(
                service.getAll(authUserId()),
                authUserCaloriesPerDay(),
                LocalTime.MIN, LocalTime.MAX);
    }

    public List<MealTo> getByDateBetweenAndTimeBetween(LocalDate startDate, LocalDate endDate,
                                                       LocalTime startTime, LocalTime endTime) {
        log.info("getByDateBetweenAndTimeBetweenWithCaloriesPerDay");
        return MealsUtil.getFilteredWithExcess(
                service.getByDateBetween(authUserId(),
                        startDate == null ? LocalDate.MIN : startDate,
                        endDate == null ? LocalDate.MAX : endDate),
                authUserCaloriesPerDay(),
                startTime == null ? LocalTime.MIN : startTime,
                endTime == null ? LocalTime.MAX : endTime);
    }
}