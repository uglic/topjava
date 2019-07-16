package ru.javawebinar.topjava.web.meal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@Controller
@RequestMapping("/meals")
public class JspMealController extends AbstractMealController {

    @Autowired
    public JspMealController(MealService service) {
        super(service);
    }

    @GetMapping(params = {"action=delete"})
    public String mealsDelete(@RequestParam("id") int id) {
        delete(id);
        return "redirect:/meals";
    }

    @GetMapping(params = {"action=create"})
    public String mealsCreate(Model model) {
        final Meal meal = new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000);
        model.addAttribute("action", "create");
        model.addAttribute("meal", meal);
        return "mealForm";
    }

    @GetMapping(params = {"action=update"})
    public String mealsUpdate(Model model, @RequestParam("id") int id) {
        final Meal meal = get(id);
        model.addAttribute("action", "update");
        model.addAttribute("meal", meal);
        return "mealForm";
    }

    @GetMapping(params = {"action=filter"})
    public String mealsFilter(Model model,
                              @RequestParam("startDate") String startDateString,
                              @RequestParam("endDate") String endDateString,
                              @RequestParam("startTime") String startTimeString,
                              @RequestParam("endTime") String endTimeString) {
        LocalDate startDate = parseLocalDate(startDateString);
        LocalDate endDate = parseLocalDate(endDateString);
        LocalTime startTime = parseLocalTime(startTimeString);
        LocalTime endTime = parseLocalTime(endTimeString);
        model.addAttribute("meals", getBetween(startDate, startTime, endDate, endTime));
        return "meals";
    }

    @GetMapping
    public String mealsAll(Model model) {
        model.addAttribute("meals", getAll());
        return "meals";
    }

    @PostMapping
    public String updateMeal(HttpServletRequest request) throws IOException {
        request.setCharacterEncoding("UTF-8");
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));

        if (StringUtils.isEmpty(request.getParameter("id"))) {
            create(meal);
        } else {
            update(meal, Integer.parseInt(Objects.requireNonNull(request.getParameter("id"))));
        }
        return "redirect:meals";
    }
}
