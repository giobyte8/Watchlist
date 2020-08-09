package com.watchlist.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.watchlist.backend.TestSecurityConfig;
import com.watchlist.backend.model.WatchlistHasMovie;
import com.watchlist.backend.security.JWTUtils;
import com.watchlist.backend.services.WatchlistMovieService;
import com.watchlist.backend.services.WatchlistService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = WatchlistMovieController.class)
@Import(TestSecurityConfig.class)
@MockBean({
        JWTUtils.class,
        RestTemplate.class,
})
public class WatchlistMovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private WatchlistMovieService watchlistMovieService;

    @MockBean
    private WatchlistService watchlistService;

    @Test
    public void testGetMovies() throws Exception {
        long watchlistId = 101L;
        List<WatchlistHasMovie> hasMovies = Collections.singletonList(
                new WatchlistHasMovie()
        );

        Mockito
                .when(watchlistService.exists(watchlistId))
                .thenReturn(true);
        Mockito
                .when(watchlistMovieService.getMovies(watchlistId))
                .thenReturn(hasMovies);

        MockHttpServletRequestBuilder reqBuilder = MockMvcRequestBuilders.get(
                "/list/{listId}/movies",
                watchlistId
        );
        MvcResult mvcResult = mockMvc
                .perform(reqBuilder)
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        WatchlistHasMovie[] recHasMovies = mapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                WatchlistHasMovie[].class
        );

        assertEquals(1, recHasMovies.length);
    }

    @Test
    public void getMoviesOfNonExistentList() throws Exception {
        long watchlistId = 501L;
        Mockito
                .when(watchlistService.exists(watchlistId))
                .thenReturn(false);

        MockHttpServletRequestBuilder reqBuilder = MockMvcRequestBuilders.get(
                "/list/{listId}/movies",
                watchlistId
        );
        mockMvc
                .perform(reqBuilder)
                .andExpect(status().isNotFound());

    }
}
