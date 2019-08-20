package ru.javawebinar.topjava.web.user;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.json.JsonUtil;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.TestUtil.readFromJson;
import static ru.javawebinar.topjava.TestUtil.userHttpBasic;
import static ru.javawebinar.topjava.UserTestData.*;

class AdminRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = AdminRestController.REST_URL + '/';

    @Test
    void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + ADMIN_ID)
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andDo(print())
                // https://jira.spring.io/browse/SPR-14472
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(contentJson(ADMIN));
    }

    @Test
    void getNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + 1)
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }

    @Test
    void getByEmail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "by?email=" + ADMIN.getEmail())
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(contentJson(ADMIN));
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + USER_ID)
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertMatch(userService.getAll(), ADMIN);
    }

    @Test
    void deleteNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + 1)
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }

    @Test
    void getUnAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(USER)))
                .andExpect(status().isForbidden());
    }

    @Test
    void update() throws Exception {
        User updated = new User(USER);
        updated.setName("UpdatedName");
        updated.setRoles(Collections.singletonList(Role.ROLE_ADMIN));
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(UserTestData.jsonWithPassword(updated, USER.getPassword()))) //.content(JsonUtil.writeValue(updated)))
                .andExpect(status().isNoContent());

        assertMatch(userService.get(USER_ID), updated);
    }

    @Test
    void createWithLocation() throws Exception {
        User expected = new User(null, "New", "new@gmail.com", "newPass", 2300, Role.ROLE_USER, Role.ROLE_ADMIN);
        ResultActions action = mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(jsonWithPassword(expected, "newPass")))
                .andExpect(status().isCreated());

        User returned = readFromJson(action, User.class);
        expected.setId(returned.getId());

        assertMatch(returned, expected);
        assertMatch(userService.getAll(), ADMIN, expected, USER);
    }

    @Test
    void getAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(contentJson(ADMIN, USER));
    }

    @Test
    void enable() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch(REST_URL + USER_ID).param("enabled", "false")
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertFalse(userService.get(USER_ID).isEnabled());
    }

    // -- update validate

    @Test
    void updateInvalidNameNull() throws Exception {
        User updated = new User(USER);
        updated.setName(null);
        updateInvalid(updated);
    }

    @Test
    void updateInvalidNameBlank() throws Exception {
        User updated = new User(USER);
        updated.setName(" \r\t\n");
        updateInvalid(updated);
    }

    @Test
    void updateInvalidNameShort() throws Exception {
        User updated = new User(USER);
        updated.setName("A");
        updateInvalid(updated);
    }

    @Test
    void updateInvalidNameLong() throws Exception {
        User updated = new User(USER);
        updated.setName(new String(new char[101]).replace('\0', 'v'));
        updateInvalid(updated);
    }

    @Test
    void updateInvalidEmailNull() throws Exception {
        User updated = new User(USER);
        updated.setEmail(null);
        updateInvalid(updated);
    }

    @Test
    void updateInvalidEmailBlank() throws Exception {
        User updated = new User(USER);
        updated.setEmail(" \r\t\n");
        updateInvalid(updated);
    }

    @Test
    void updateInvalidEmailLong() throws Exception {
        User updated = new User(USER);
        updated.setEmail(new String(new char[101]).replace('\0', 'v'));
        updateInvalid(updated);
    }

    @Test
    void updateInvalidEmailNotEmail() throws Exception {
        User updated = new User(USER);
        updated.setEmail("sobaka-sobake-rozn'\0");
        updateInvalid(updated);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void updateInvalidEmailNotUnique() throws Exception {
        User updated = new User(USER);
        updated.setEmail(ADMIN.getEmail());
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(UserTestData.jsonWithPassword(updated, USER.getPassword()))) //.content(JsonUtil.writeValue(updated)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateInvalidPasswordNull() throws Exception {
        User updated = new User(USER);
        updated.setPassword(null);
        updateInvalid(updated);
    }

    @Test
    void updateInvalidPasswordBlank() throws Exception {
        User updated = new User(USER);
        updated.setPassword("\r\n ");
        updateInvalid(updated);
    }

    @Test
    void updateInvalidPasswordShort() throws Exception {
        User updated = new User(USER);
        updated.setPassword("1234");
        updateInvalid(updated);
    }

    @Test
    void updateInvalidPasswordLong() throws Exception {
        User updated = new User(USER);
        updated.setPassword(new String(new char[101]).replace('\0', 'a'));
        updateInvalid(updated);
    }

    @Test
    void updateInvalidCaloriesPerDayLow() throws Exception {
        User updated = new User(USER);
        updated.setCaloriesPerDay(9);
        updateInvalid(updated);
    }

    @Test
    void updateInvalidCaloriesPerDayHigh() throws Exception {
        User updated = new User(USER);
        updated.setCaloriesPerDay(10001);
        updateInvalid(updated);
    }

    // -- create validate

    @Test
    void createWithLocationInvalidNameNull() throws Exception {
        User expected = new User(null, null, "new@gmail.com", "newPass", 2300, Role.ROLE_USER, Role.ROLE_ADMIN);
        createWithLocationInvalid(expected);
    }

    @Test
    void createWithLocationInvalidNameBlank() throws Exception {
        User expected = new User(null, " \r\t\n", "new@gmail.com", "newPass", 2300, Role.ROLE_USER, Role.ROLE_ADMIN);
        createWithLocationInvalid(expected);
    }

    @Test
    void createWithLocationInvalidNameShort() throws Exception {
        User expected = new User(null, "A", "new@gmail.com", "newPass", 2300, Role.ROLE_USER, Role.ROLE_ADMIN);
        createWithLocationInvalid(expected);
    }

    @Test
    void createWithLocationInvalidNameLong() throws Exception {
        User expected = new User(null, new String(new char[101]).replace('\0', 'v'), "new@gmail.com", "newPass", 2300, Role.ROLE_USER, Role.ROLE_ADMIN);
        createWithLocationInvalid(expected);
    }

    @Test
    void createWithLocationInvalidEmailNull() throws Exception {
        User expected = new User(null, "New", null, "newPass", 2300, Role.ROLE_USER, Role.ROLE_ADMIN);
        createWithLocationInvalid(expected);
    }

    @Test
    void createWithLocationInvalidEmailBlank() throws Exception {
        User expected = new User(null, "New", " \r\t\n", "newPass", 2300, Role.ROLE_USER, Role.ROLE_ADMIN);
        createWithLocationInvalid(expected);
    }

    @Test
    void createWithLocationInvalidEmailLong() throws Exception {
        User expected = new User(null, "New", new String(new char[101]).replace('\0', 'v'), "newPass", 2300, Role.ROLE_USER, Role.ROLE_ADMIN);
        createWithLocationInvalid(expected);
    }

    @Test
    void createWithLocationInvalidEmailNotEmail() throws Exception {
        User expected = new User(null, "New", "sobaka-sobake-rozn'\0", "newPass", 2300, Role.ROLE_USER, Role.ROLE_ADMIN);
        createWithLocationInvalid(expected);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void createWithLocationInvalidEmailNotUnique() throws Exception {
        User expected = new User(null, "New", USER.getEmail(), "newPass", 2300, Role.ROLE_USER, Role.ROLE_ADMIN);
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(jsonWithPassword(expected, expected.getPassword())))
                .andExpect(status().isConflict());
    }

    @Test
    void createWithLocationInvalidPasswordNull() throws Exception {
        User expected = new User(null, "New", "new@gmail.com", null, 2300, Role.ROLE_USER, Role.ROLE_ADMIN);
        createWithLocationInvalid(expected);
    }

    @Test
    void createWithLocationInvalidPasswordBlank() throws Exception {
        User expected = new User(null, "New", "new@gmail.com", "\r\n \t", 2300, Role.ROLE_USER, Role.ROLE_ADMIN);
        createWithLocationInvalid(expected);
    }

    @Test
    void createWithLocationInvalidPasswordShort() throws Exception {
        User expected = new User(null, "New", "new@gmail.com", "1234", 2300, Role.ROLE_USER, Role.ROLE_ADMIN);
        createWithLocationInvalid(expected);
    }

    @Test
    void createWithLocationInvalidPasswordLong() throws Exception {
        User expected = new User(null, "New", "new@gmail.com", new String(new char[101]).replace('\0', 'a'), 2300, Role.ROLE_USER, Role.ROLE_ADMIN);
        createWithLocationInvalid(expected);
    }

    @Test
    void createWithLocationInvalidCaloriesPerDayLow() throws Exception {
        User expected = new User(null, "New", "new@gmail.com", "newPass", 9, Role.ROLE_USER, Role.ROLE_ADMIN);
        createWithLocationInvalid(expected);
    }

    @Test
    void createWithLocationInvalidCaloriesPerDayHigh() throws Exception {
        User expected = new User(null, "New", "new@gmail.com", "newPass", 10001, Role.ROLE_USER, Role.ROLE_ADMIN);
        createWithLocationInvalid(expected);
    }

    // --- validation subroutines

    void updateInvalid(User testUser) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(JsonUtil.writeValue(testUser)))
                .andExpect(status().isUnprocessableEntity());
    }

    void createWithLocationInvalid(User testUser) throws Exception {
        //User expected = new User(null, "New", "new@gmail.com", "newPass",  2300, Role.ROLE_USER, Role.ROLE_ADMIN);
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(jsonWithPassword(testUser, testUser.getPassword())))
                .andExpect(status().isUnprocessableEntity());
    }
}