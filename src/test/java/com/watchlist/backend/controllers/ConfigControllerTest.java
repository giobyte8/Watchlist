package com.watchlist.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.watchlist.backend.TestSecurityConfig;
import com.watchlist.backend.entities.json.Config;
import com.watchlist.backend.security.JWTUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@Import(TestSecurityConfig.class)
@MockBean({
        JWTUtils.class,
        RestTemplate.class
})
@WebMvcTest(
        controllers = ConfigController.class,
        properties = {
                "watchlist.tmdb-api-key=fake-tmdb-key",
                "watchlist.yt-api-key=fake-yt-key"
        }
)
public class ConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGet() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tmdb_api_key").isString())
                .andExpect(jsonPath("$.yt_api_key").isString())
                .andReturn();

        Config config = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                Config.class
        );

        assertEquals(config.getTmdbApiKey(), "fake-tmdb-key");
        assertEquals(config.getYtApiKey(), "fake-yt-key");
    }
}
