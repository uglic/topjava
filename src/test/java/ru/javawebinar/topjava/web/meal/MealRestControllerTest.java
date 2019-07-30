package ru.javawebinar.topjava.web.meal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.AbstractBaseEntity;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.SecurityUtil;
import ru.javawebinar.topjava.web.json.JsonUtil;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.TestUtil.readFromJson;
import static ru.javawebinar.topjava.UserTestData.USER;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

class MealRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = MealRestController.REST_URL + "/";

    @Autowired
    private MealService mealService;

    @Test
    void testGet() throws Exception {
        SecurityUtil.setAuthUserId(USER_ID);
        mockMvc.perform(get(REST_URL + MEAL1_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(MealTestData.contentJson(MealTestData.MEAL1));
    }

    @Test
    void testCreateWithLocation() throws Exception {
        SecurityUtil.setAuthUserId(USER_ID);
        Meal expected = new Meal(null, LocalDateTime.now(), "New", 50);
        ResultActions action = mockMvc.perform(post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(expected)))
                .andExpect(status().isCreated());

        Meal returned = readFromJson(action, Meal.class);
        expected.setId(returned.getId());
        expected.setUser(USER);
        MealTestData.assertMatch(returned, expected);
        MealTestData.assertMatch(mealService.getAll(USER_ID), returned, MEAL6, MEAL5, MEAL4, MEAL3, MEAL2, MEAL1);
    }

    @Test
    void testDelete() throws Exception {
        SecurityUtil.setAuthUserId(USER_ID);
        mockMvc.perform(delete(REST_URL + MEAL1_ID))
                .andExpect(status().isNoContent());
        assertMatch(mealService.getAll(USER_ID), MEAL6, MEAL5, MEAL4, MEAL3, MEAL2);
    }

    @Test
    void testUpdate() throws Exception {
        SecurityUtil.setAuthUserId(USER_ID);
        Meal updated = new Meal(MEAL1_ID, LocalDateTime.now(), "New", 50);
        mockMvc.perform(put(REST_URL + MEAL1_ID).contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isNoContent());

        updated.setUser(USER);
        assertMatch(mealService.get(MEAL1_ID, USER_ID), updated);
    }

    @Test
    void testGetAll() throws Exception {
        SecurityUtil.setAuthUserId(USER_ID);
        mockMvc.perform(get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(MealTestData.contentJsonTo(MealsUtil.getWithExcess(MEALS,
                        USER.getCaloriesPerDay())));
    }

    @Test
    void testGetBetween() throws Exception {
        SecurityUtil.setAuthUserId(USER_ID);
        mockMvc.perform(get(REST_URL + "by?"
                        + "startDate={startDate}&endDate={endDate}&startTime={startTime}&endTime={endTime}",
                "2015-05-30", "2015-05-30", "20:00", "20:00"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(MealTestData.contentJsonTo(MealsUtil.getWithExcess(Collections.singletonList(MEAL3),
                        USER.getCaloriesPerDay())));
    }

    @Test
    void testGetBetweenEmptyStartDate() throws Exception {
        SecurityUtil.setAuthUserId(USER_ID);
        mockMvc.perform(get(REST_URL + "by?"
                        + "startDate={startDate}&endDate={endDate}&startTime={startTime}&endTime={endTime}",
                "", "2015-05-31", "20:00", "20:00"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(MealTestData.contentJsonTo(getFilteredMealTo(USER, MEAL6, MEAL3)));
    }

    @Test
    void testGetBetweenEmptyEndDate() throws Exception {
        SecurityUtil.setAuthUserId(USER_ID);
        mockMvc.perform(get(REST_URL + "by?"
                        + "startDate={startDate}&endDate={endDate}&startTime={startTime}&endTime={endTime}",
                "2015-05-30", "", "13:00", "20:00"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(MealTestData.contentJsonTo(getFilteredMealTo(USER, MEAL6, MEAL5, MEAL3, MEAL2)));
    }

    @Test
    void testGetBetweenEmptyStartTime() throws Exception {
        SecurityUtil.setAuthUserId(USER_ID);
        mockMvc.perform(get(REST_URL + "by?"
                        + "startDate={startDate}&endDate={endDate}&startTime={startTime}&endTime={endTime}",
                "2015-05-30", "2015-05-30", "", "13:00"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(MealTestData.contentJsonTo(getFilteredMealTo(USER, MEAL2, MEAL1)));
    }

    @Test
    void testGetBetweenEmptyEndTime() throws Exception {
        SecurityUtil.setAuthUserId(USER_ID);
        mockMvc.perform(get(REST_URL + "by?"
                        + "startDate={startDate}&endDate={endDate}&startTime={startTime}&endTime={endTime}",
                "2015-05-30", "2015-05-31", "20:00", ""))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(MealTestData.contentJsonTo(getFilteredMealTo(USER, MEAL6, MEAL3)));
    }

    private List<MealTo> getFilteredMealTo(User user, Meal... meals) {
        return MealsUtil.getWithExcess(MEALS,
                user.getCaloriesPerDay()).stream()
                .filter(m -> Arrays.stream(meals)
                        .map(AbstractBaseEntity::getId)
                        .collect(Collectors.toList())
                        .contains(m.getId()))
                .collect(Collectors.toList());
    }
}