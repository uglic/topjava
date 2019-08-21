package ru.javawebinar.topjava.web.user;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.to.UserTo;
import ru.javawebinar.topjava.util.UserUtil;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.json.JsonUtil;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.TestUtil.readFromJson;
import static ru.javawebinar.topjava.TestUtil.userHttpBasic;
import static ru.javawebinar.topjava.UserTestData.*;
import static ru.javawebinar.topjava.web.user.ProfileRestController.REST_URL;

class ProfileRestControllerTest extends AbstractControllerTest {

    @Test
    void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(USER)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(contentJson(USER));
    }

    @Test
    void getUnAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL)
                .with(userHttpBasic(USER)))
                .andExpect(status().isNoContent());
        assertMatch(userService.getAll(), ADMIN);
    }

    @Test
    void register() throws Exception {
        UserTo createdTo = new UserTo(null, "newName", "newemail@ya.ru", "newPassword", 1500);

        ResultActions action = mockMvc.perform(MockMvcRequestBuilders.post(REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(createdTo)))
                .andDo(print())
                .andExpect(status().isCreated());
        User returned = readFromJson(action, User.class);

        User created = UserUtil.createNewFromTo(createdTo);
        created.setId(returned.getId());

        assertMatch(returned, created);
        assertMatch(userService.getByEmail("newemail@ya.ru"), created);
    }

    @Test
    void update() throws Exception {
        UserTo updatedTo = new UserTo(null, "newName", "newemail@ya.ru", "newPassword", 1500);
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL).contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(USER))
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertMatch(userService.getByEmail("newemail@ya.ru"), UserUtil.updateFromTo(new User(USER), updatedTo));
    }
    // -- update validate

    @Test
    void updateInvalidNameNull() throws Exception {
        UserTo updated = new UserTo(null, null, "newemail@ya.ru", "newPassword", 1500);
        updateInvalid(updated);
    }

    @Test
    void updateInvalidNameBlank() throws Exception {
        UserTo updated = new UserTo(null, " \r\t\n", "newemail@ya.ru", "newPassword", 1500);
        updateInvalid(updated);
    }

    @Test
    void updateInvalidNameShort() throws Exception {
        UserTo updated = new UserTo(null, "A", "newemail@ya.ru", "newPassword", 1500);
        updateInvalid(updated);
    }

    @Test
    void updateInvalidNameLong() throws Exception {
        UserTo updated = new UserTo(null, new String(new char[101]).replace('\0', 'v'), "newemail@ya.ru", "newPassword", 1500);
        updateInvalid(updated);
    }

    @Test
    void updateInvalidEmailNull() throws Exception {
        UserTo updated = new UserTo(null, "newName", null, "newPassword", 1500);
        updateInvalid(updated);
    }

    @Test
    void updateInvalidEmailBlank() throws Exception {
        UserTo updated = new UserTo(null, "newName", " \r\t\n", "newPassword", 1500);
        updateInvalid(updated);
    }

    @Test
    void updateInvalidEmailLong() throws Exception {
        UserTo updated = new UserTo(null, "newName", "name@" + new String(new char[96]).replace('\0', 'v'), "newPassword", 1500);
        updateInvalid(updated);
    }

    @Test
    void updateInvalidEmailNotEmail() throws Exception {
        UserTo updated = new UserTo(null, "newName", "sobaka-sobake-rozn'\0", "newPassword", 1500);
        updateInvalid(updated);
    }

    @Test
    void updateInvalidEmailNotUnique() throws Exception {
        UserTo updated = new UserTo(null, "newName", ADMIN.getEmail(), "newPassword", 1500);
        updateInvalid(updated);
    }

    @Test
    void updateInvalidPasswordNull() throws Exception {
        UserTo updated = new UserTo(null, "newName", "newemail@ya.ru", null, 1500);
        updateInvalid(updated);
    }

    @Test
    void updateInvalidPasswordBlank() throws Exception {
        UserTo updated = new UserTo(null, "newName", "newemail@ya.ru", "\r\n ", 1500);
        updateInvalid(updated);
    }

    @Test
    void updateInvalidPasswordShort() throws Exception {
        UserTo updated = new UserTo(null, "newName", "newemail@ya.ru", "1234", 1500);
        updateInvalid(updated);
    }

    @Test
    void updateInvalidPasswordLong() throws Exception {
        UserTo updated = new UserTo(null, "newName", "newemail@ya.ru", new String(new char[101]).replace('\0', 'a'), 1500);
        updateInvalid(updated);
    }

    @Test
    void updateInvalidCaloriesPerDayLow() throws Exception {
        UserTo updated = new UserTo(null, "newName", "newemail@ya.ru", "newPassword", 9);
        updateInvalid(updated);
    }

    @Test
    void updateInvalidCaloriesPerDayHigh() throws Exception {
        UserTo updated = new UserTo(null, "newName", "newemail@ya.ru", "newPassword", 10001);
        updateInvalid(updated);
    }

    // -- create validate

    @Test
    void createWithLocationInvalidNameNull() throws Exception {
        UserTo expected = new UserTo(null, null, "newemail@ya.ru", "newPassword", 1500);
        registerInvalid(expected);
    }

    @Test
    void createWithLocationInvalidNameBlank() throws Exception {
        UserTo expected = new UserTo(null, " \r\t\n", "newemail@ya.ru", "newPassword", 1500);
        registerInvalid(expected);
    }

    @Test
    void createWithLocationInvalidNameShort() throws Exception {
        UserTo expected = new UserTo(null, "A", "newemail@ya.ru", "newPassword", 1500);
        registerInvalid(expected);
    }

    @Test
    void createWithLocationInvalidNameLong() throws Exception {
        UserTo expected = new UserTo(null, new String(new char[101]).replace('\0', 'v'), "newemail@ya.ru", "newPassword", 1500);
        registerInvalid(expected);
    }

    @Test
    void createWithLocationInvalidEmailNull() throws Exception {
        UserTo expected = new UserTo(null, "newName", null, "newPassword", 1500);
        registerInvalid(expected);
    }

    @Test
    void createWithLocationInvalidEmailBlank() throws Exception {
        UserTo expected = new UserTo(null, "newName", " \r\t\n", "newPassword", 1500);
        registerInvalid(expected);
    }

    @Test
    void createWithLocationInvalidEmailLong() throws Exception {
        UserTo expected = new UserTo(null, "newName", "name@" + new String(new char[96]).replace('\0', 'v'), "newPassword", 1500);
        registerInvalid(expected);
    }

    @Test
    void createWithLocationInvalidEmailNotEmail() throws Exception {
        UserTo expected = new UserTo(null, "newName", "sobaka-sobake-rozn'\0", "newPassword", 1500);
        registerInvalid(expected);
    }

    @Test
    void createWithLocationInvalidEmailNotUnique() throws Exception {
        UserTo expected = new UserTo(null, "newName", USER.getEmail(), "newPassword", 1500);
        registerInvalid(expected);
    }

    @Test
    void createWithLocationInvalidPasswordNull() throws Exception {
        UserTo expected = new UserTo(null, "newName", "newemail@ya.ru", null, 1500);
        registerInvalid(expected);
    }

    @Test
    void createWithLocationInvalidPasswordBlank() throws Exception {
        UserTo expected = new UserTo(null, "newName", "newemail@ya.ru", "\r\n \t", 1500);
        registerInvalid(expected);
    }

    @Test
    void createWithLocationInvalidPasswordShort() throws Exception {
        UserTo expected = new UserTo(null, "newName", "newemail@ya.ru", "1234", 1500);
        registerInvalid(expected);
    }

    @Test
    void createWithLocationInvalidPasswordLong() throws Exception {
        UserTo expected = new UserTo(null, "newName", "newemail@ya.ru", new String(new char[101]).replace('\0', 'a'), 1500);
        registerInvalid(expected);
    }

    @Test
    void createWithLocationInvalidCaloriesPerDayLow() throws Exception {
        UserTo expected = new UserTo(null, "newName", "newemail@ya.ru", "newPassword", 9);
        registerInvalid(expected);
    }

    @Test
    void createWithLocationInvalidCaloriesPerDayHigh() throws Exception {
        UserTo expected = new UserTo(null, "newName", "newemail@ya.ru", "newPassword", 10001);
        registerInvalid(expected);
    }

    // --- validation subroutines

    void updateInvalid(UserTo testUser) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL).contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(USER))
                .content(JsonUtil.writeValue(testUser)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    void registerInvalid(UserTo testUser) throws Exception {
        //UserTo createdTo = new UserTo(null, "newName", "newemail@ya.ru", "newPassword", 1500);
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(testUser)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }
}