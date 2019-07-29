package ru.javawebinar.topjava.web;

import org.hamcrest.Matcher;
import org.hamcrest.core.StringStartsWith;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ResourceControllerTest extends AbstractControllerTest {

    @Test
    void testGetStyles() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/resources/css/style.css"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", StringStartsWith.startsWith("text/css")));
    }
}
