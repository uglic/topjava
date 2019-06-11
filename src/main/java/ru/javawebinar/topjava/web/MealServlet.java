package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.data.MealData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.storage.IStorage;
import ru.javawebinar.topjava.storage.MealMemoryStorage;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private static final String EDIT = "mealEdit.jsp";
    private static final String LIST = "mealList.jsp";
    private static final int CALORIES_PER_DAY = 1300;

    private IStorage<Meal, Integer> storage;

    @Override
    public void init() {
        storage = new MealMemoryStorage();
        MealData.getData().forEach(m -> storage.add(m));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String forwardPath;
        response.setCharacterEncoding("utf-8");
        String action = request.getParameter("action");
        if (action == null) action = "";
        switch (action) {
            case "add":
            case "edit":
                request.setAttribute("returnUri", request.getRequestURI());
                request.setAttribute("action", action);
                request.setAttribute("mealValue", "edit".equals(action)
                        ? storage.get(Integer.parseInt(request.getParameter("id")))
                        : null);
                forwardPath = EDIT;
                break;
            case "delete":
                storage.delete(Integer.parseInt(request.getParameter("id")));
                log.debug("delete meal");
                response.sendRedirect(request.getRequestURI());
                return;
            default:
                log.debug("redirect to meal list");
                request.setAttribute("mealsList", MealsUtil.getFilteredWithExcess(
                        storage.getAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY));
                forwardPath = LIST;
        }
        request.getRequestDispatcher(forwardPath).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("utf-8");
        String action = request.getParameter("action");
        if ("edit".equals(action) || "add".equals(action)) {
            Meal meal = new Meal(
                    "edit".equals(action)
                            ? Integer.parseInt(request.getParameter("id"))
                            : 0,
                    LocalDateTime.parse(request.getParameter("dateTime")),
                    request.getParameter("description"),
                    Integer.parseInt(request.getParameter("calories")));
            if (meal.getId() == 0) {
                storage.add(meal);
                log.debug("created meal");
            } else {
                storage.update(meal);
                log.debug("updated meal id = " + meal.getId());
            }
        }
        response.sendRedirect(request.getRequestURI());
    }
}