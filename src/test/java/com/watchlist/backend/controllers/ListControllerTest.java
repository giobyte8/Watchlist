package com.watchlist.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.watchlist.backend.TestSecurityConfig;
import com.watchlist.backend.model.Watchlist;
import com.watchlist.backend.security.JWTUtils;
import com.watchlist.backend.services.UserService;
import com.watchlist.backend.services.WatchlistService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ListController.class)
@Import(TestSecurityConfig.class)
@MockBean({
        JWTUtils.class,
        RestTemplate.class
})
public class ListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private WatchlistService watchlistService;

    @Test
    public void testGetAll() throws Exception {
        List<Watchlist> lists = new ArrayList<>();
        long userId = 1;

        Mockito
                .when(userService.getLists(userId))
                .thenReturn(lists);

        String url = String.format("/user/%d/lists", userId);
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        Mockito
                .verify(userService, times(1))
                .getLists(userId);
    }

    @Test
    public void testGetAllNonEmpty() throws Exception {
        long userId = 2;

        Watchlist watchlist1 = new Watchlist();
        watchlist1.setId(1L);

        Watchlist watchlist2 = new Watchlist();
        watchlist2.setId(2L);

        List<Watchlist> watchlists = Arrays.asList(
                watchlist1,
                watchlist2
        );

        Mockito
                .when(userService.getLists(userId))
                .thenReturn(watchlists);

        String url = String.format("/user/%d/lists", userId);
        MvcResult mvcResult = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        List<Watchlist> resultList = Arrays.asList(objectMapper.readValue(
                result,
                Watchlist[].class
        ));

        assertEquals(resultList.size(), watchlists.size());
        for (Watchlist watchlist : watchlists) {
            assertTrue(resultList.contains(watchlist));
        }
    }

    @Test
    public void testGetAllNonexistentUser() throws Exception {
        long userId = 1L;

        Mockito
                .when(userService.getLists(1))
                .thenReturn(null);

        String url = String.format("/user/%d/lists", userId);
        mockMvc.perform(get(url))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testCreate() throws Exception {
        Watchlist watchlist = new Watchlist();
        watchlist.setName("Western");

        long userId = 101L;

        MockHttpServletRequestBuilder reqBuilder = post(
                    "/user/{userId}/lists",
                    userId
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(watchlist));

        MvcResult mvcResult = mockMvc.perform(reqBuilder)
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        Watchlist resultWatchlist = objectMapper.readValue(
                result,
                Watchlist.class
        );

        Mockito
                .verify(watchlistService, times(1))
                .create(
                        argThat(Objects::nonNull),
                        ArgumentMatchers.eq(userId)
                );
        assertEquals(watchlist.getName(), resultWatchlist.getName());
    }

    @Test
    public void testCreateWithoutName() throws Exception {
        verifyValidationError(new Watchlist());
    }

    @Test
    public void testCreateWithEmptyName() throws Exception {
        Watchlist watchlist = new Watchlist();
        watchlist.setName("");

        verifyValidationError(watchlist);
    }

    @Test
    public void testCreateWithBlankName() throws Exception {
        Watchlist watchlist = new Watchlist();
        watchlist.setName(" ");

        verifyValidationError(watchlist);
    }

    private void verifyValidationError(Watchlist watchlist) throws Exception {
        MockHttpServletRequestBuilder reqBuilder = post(
                    "/user/{userId}/lists",
                    1L
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(watchlist));

        mockMvc.perform(reqBuilder)
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.errors").isArray());
    }
}
