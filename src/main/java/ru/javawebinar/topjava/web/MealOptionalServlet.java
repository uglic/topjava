package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.data.MealData;
import ru.javawebinar.topjava.model.IStorage;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealMemoryStorage;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.TimeUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class MealOptionalServlet extends HttpServlet {
    private static final Logger log = getLogger(MealOptionalServlet.class);
    private static final String VIEW = "mealView.jsp";
    private static final String EDIT = "mealEdit.jsp";
    private static final String ADD = "mealEdit.jsp"; //mealAdd.jsp
    private static final String LIST = "mealList.jsp";
    private static final int CALORIES_PER_DAY = 1300;

    private IStorage<Meal, Integer> mealMemoryStorage;

    @Override
    public void init() throws ServletException {
        super.init();
        for (Meal m : MealData.getData()) {
            mealMemoryStorage.add(m);
        }
    }

    public MealOptionalServlet() {
        super();
        mealMemoryStorage = new MealMemoryStorage();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String action = request.getParameter("action");
        int id = getIntValueOrZero(request.getParameter("id"));
        MealTo meal = null;
        action = (action == null) ? "" : action.toLowerCase();
        if ("view".equals(action) || "edit".equals(action)) {
            meal = getMealToFromStorage(id);
            request.setAttribute("mealValue", meal);
        }
        String forwardPath = "";
        boolean redirectToList = false;
        if ("add".equals(action)) {
            log.debug("redirect to add meal");
            forwardPath = ADD;
        } else if ("view".equals(action) && meal != null) {
            log.debug("redirect to view meal with id = " + id);
            forwardPath = VIEW;
        } else if ("edit".equals(action) && meal != null) {
            log.debug("redirect to edit meal with id = " + id);
            forwardPath = EDIT;
        } else {
            if ("delete".equals(action) && id != 0) {
                mealMemoryStorage.delete(id);
                log.debug("delete meal with id = " + id);
            }
            redirectToList = true;
        }
        if (redirectToList) {
            makeGetList(request, response);
        } else {
            request.setAttribute("dayNorm", CALORIES_PER_DAY);
            request.getRequestDispatcher(forwardPath).forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String action = request.getParameter("action");
        if ("edit".equalsIgnoreCase(action) || "add".equalsIgnoreCase(action)) {
            LocalDateTime newDateTime = getDateTime(request.getParameter("date"), request.getParameter("time"));
            int newCalories = getIntValueOrZero(request.getParameter("calories"));
            int newId = getIntValueOrZero(request.getParameter("id"));
            Meal meal = new Meal(newId, newDateTime, request.getParameter("description"), newCalories);
            if (newId == 0) {
                mealMemoryStorage.add(meal);
                log.debug("created meal at " + TimeUtil.formatTimeDate(newDateTime));
            } else {
                mealMemoryStorage.update(meal);
                log.debug("updated meal id = " + newId);
            }
        }
        makeGetList(request, response);
    }

    private void makeGetList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("redirect to meal list");
        request.setAttribute("mealsList", MealsUtil.getFilteredWithExcess(
                mealMemoryStorage.getAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY));
        request.setAttribute("dayNorm", CALORIES_PER_DAY);
        request.getRequestDispatcher(LIST).forward(request, response);
    }

    private LocalDateTime getDateTime(String date, String time) {
        LocalDate newDate;
        try {
            newDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        } catch (DateTimeParseException e) {
            newDate = LocalDate.now();
        }
        LocalTime newTime;
        try {
            newTime = LocalTime.parse(time, TimeUtil.TIME_FORMAT);
        } catch (DateTimeParseException e) {
            newTime = LocalTime.now(); // without time zone, not for production
        }
        return LocalDateTime.of(newDate, newTime);
    }

    private MealTo getMealToFromStorage(int id) {
        return MealsUtil
                .getFilteredWithExcess(getAllForSameDate(mealMemoryStorage, id),
                        LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY)
                .stream()
                .filter(s -> s.getId() == id)
                .findAny()
                .orElse(null);
    }

    private List<Meal> getAllForSameDate(IStorage<Meal, Integer> storage, final int id) {
        LocalDate dateFor = storage.get(id).getDate();
        List<Meal> mealList = storage.getAll();
        return mealList.stream()
                .filter(s -> s.getDate().equals(dateFor))
                .collect(Collectors.toList());
    }

    private int getIntValueOrZero(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException | NullPointerException e) {
            return 0;
        }
    }
}