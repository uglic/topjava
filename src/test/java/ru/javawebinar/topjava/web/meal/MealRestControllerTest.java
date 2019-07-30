package ru.javawebinar.topjava.web.meal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.SecurityUtil;
import ru.javawebinar.topjava.web.json.JsonUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

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
                        MealsUtil.DEFAULT_CALORIES_PER_DAY)));
    }

    @Test
    void testGetBetween() throws Exception {
        LocalDateTime localDateTime = MEAL3.getDateTime();
        SecurityUtil.setAuthUserId(USER_ID);
        mockMvc.perform(get(REST_URL + "by?"
                        + "startDate={startDate}&endDate={endDate}&startTime={startTime}&endTime={endTime}",
                DateTimeFormatter.ISO_LOCAL_DATE.format(localDateTime),
                DateTimeFormatter.ISO_LOCAL_DATE.format(localDateTime),
                DateTimeFormatter.ISO_LOCAL_TIME.format(localDateTime),
                DateTimeFormatter.ISO_LOCAL_TIME.format(localDateTime)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(MealTestData.contentJsonTo(MealsUtil.getWithExcess(Collections.singletonList(MEAL3),
                        MealsUtil.DEFAULT_CALORIES_PER_DAY)));
    }
}