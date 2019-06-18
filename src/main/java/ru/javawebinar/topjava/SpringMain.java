package ru.javawebinar.topjava;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.web.meal.MealRestController;
import ru.javawebinar.topjava.web.user.AdminRestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

public class SpringMain {
    public static void main(String[] args) {
        // java 7 automatic resource management
        try (ConfigurableApplicationContext appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml")) {
            System.out.println("Bean definition names: " + Arrays.toString(appCtx.getBeanDefinitionNames()));
            AdminRestController adminUserController = appCtx.getBean(AdminRestController.class);
            adminUserController.create(new User(null, "userName", "email@mail.ru", "password", Role.ROLE_ADMIN));

            MealRestController mealController = appCtx.getBean(MealRestController.class);
            System.out.println(mealController.getAll());
            System.out.println(mealController.get(1));
            //System.out.println("MUST ERROR: " + mealController.get(7));
            System.out.println(mealController.create(new Meal(LocalDateTime.now(), "Yes!", 2100)));
            System.out.println(mealController.get(7));
            //System.out.println("MUST ERROR: " + mealController.get(8));
            //mealController.delete(7); // used in final result
            //mealController.delete(8); // MUST ERROR
            Meal mealExisted = mealController.get(1);
            Meal mealForUpdate;
            mealForUpdate = new Meal(mealExisted.getId(), mealExisted.getDateTime(), mealExisted.getDescription() + " UPD", mealExisted.getCalories() + 2000);
            mealController.update(mealForUpdate);
            System.out.println(mealController.get(1));
            System.out.println(mealController.getByDateBetweenAndTimeBetween(
                    LocalDate.now(),
                    LocalDate.now(),
                    LocalTime.of(LocalTime.now().getHour(), 0),
                    LocalTime.of(LocalTime.now().getHour() + 1, 0)));
        }
    }
}
