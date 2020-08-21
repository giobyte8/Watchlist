package com.watchlist.backend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.watchlist.backend.TestSecurityConfig;
import com.watchlist.backend.entities.MoviePost;
import com.watchlist.backend.entities.UpdateWatchlistHasMovie;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
                "/lists/{listId}/movies",
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
                "/lists/{listId}/movies",
                watchlistId
        );
        mockMvc
                .perform(reqBuilder)
                .andExpect(status().isNotFound());

    }

    @Test
    public void testAddMovie() throws Exception {
        long listId = 1;
        int tmdbId = 104;
        long hasMovieId = 50001;

        MoviePost moviePost = new MoviePost();
        moviePost.setAddedBy(1);
        moviePost.setTmdbId(tmdbId);

        WatchlistHasMovie hasMovie = new WatchlistHasMovie();
        hasMovie.setId(hasMovieId);

        Mockito
                .when(watchlistService.exists(listId))
                .thenReturn(true);
        Mockito
                .when(watchlistMovieService.existsByWatchlistAndTmdbId(
                        listId,
                        tmdbId
                ))
                .thenReturn(false);
        Mockito
                .when(watchlistMovieService.addMovie(
                        anyLong(),
                        any(MoviePost.class)
                ))
                .thenReturn(hasMovie);

        String reqBody = mapper.writeValueAsString(moviePost);
        MvcResult mvcResult = mockMvc
                .perform(
                        post(
                                "/lists/{listId}/movies",
                                listId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(reqBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        WatchlistHasMovie responseHasMovie = mapper.readValue(
                response,
                WatchlistHasMovie.class
        );
        assertEquals(hasMovieId, responseHasMovie.getId());
    }

    @Test
    public void testAddMovieWithoutValues() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = makeRequestBuilder(
                new MoviePost(),
                1
        );

        mockMvc.perform(reqBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    public void testAddMovieToNonExistentList() throws Exception {
        long listId = 501;
        int tmdbId = 105;

        MoviePost moviePost = new MoviePost();
        moviePost.setAddedBy(1);
        moviePost.setTmdbId(tmdbId);

        MockHttpServletRequestBuilder reqBuilder = makeRequestBuilder(
                moviePost,
                listId
        );

        Mockito
                .when(watchlistService.exists(listId))
                .thenReturn(false);

        mockMvc
                .perform(reqBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAddDuplicatedMovie() throws Exception {
        long listId = 501;
        int tmdbId = 105;

        MoviePost moviePost = new MoviePost();
        moviePost.setAddedBy(1);
        moviePost.setTmdbId(tmdbId);

        MockHttpServletRequestBuilder reqBuilder = makeRequestBuilder(
                moviePost,
                listId
        );

        Mockito
                .when(watchlistService.exists(listId))
                .thenReturn(true);
        Mockito
                .when(watchlistMovieService.existsByWatchlistAndTmdbId(
                        listId,
                        tmdbId
                ))
                .thenReturn(true);

        mockMvc
                .perform(reqBuilder)
                .andExpect(status().isConflict());
    }

    @Test
    public void testUpdateMovie() throws Throwable {
        long listId = 101;
        long hasMovieId = 100000;

        Date seenAt = new Date();

        WatchlistHasMovie hasMovie = new WatchlistHasMovie();
        hasMovie.setId(hasMovieId);
        hasMovie.setSeenAt(seenAt);

        UpdateWatchlistHasMovie updateHasMovie = new UpdateWatchlistHasMovie();
        updateHasMovie.setSeenAt(seenAt);

        Mockito
                .when(watchlistService.exists(listId))
                .thenReturn(true);
        Mockito
                .when(watchlistMovieService.exists(hasMovieId))
                .thenReturn(true);
        Mockito
                .when(watchlistMovieService.updateHasMovie(
                        any(UpdateWatchlistHasMovie.class)
                ))
                .thenReturn(hasMovie);

        MockHttpServletRequestBuilder reqBuilder = makeRequestBuilder(
                updateHasMovie,
                listId,
                hasMovieId
        );
        MvcResult mvcResult = mockMvc.perform(reqBuilder)
                .andExpect(status().isOk())
                .andReturn();

        String jHasMovie = mvcResult.getResponse().getContentAsString();
        WatchlistHasMovie rHasMovie = mapper.readValue(
                jHasMovie,
                WatchlistHasMovie.class
        );

        assertEquals(
                rHasMovie.getSeenAt(),
                updateHasMovie.getSeenAt()
        );
    }

    @Test
    public void testUpdateMovieNonExistentList() throws Throwable {
        long listId = 101;
        long hasMovieId = 100000;

        UpdateWatchlistHasMovie updateHasMovie = new UpdateWatchlistHasMovie();
        updateHasMovie.setSeenAt(new Date());

        Mockito
                .when(watchlistService.exists(listId))
                .thenReturn(false);

        MockHttpServletRequestBuilder reqBuilder = makeRequestBuilder(
                updateHasMovie,
                listId,
                hasMovieId
        );

        mockMvc.perform(reqBuilder)
                .andExpect(status()
                .isNotFound());
    }

    @Test
    public void testUpdateNonExistentWatchlistHasMovie() throws Throwable {
        long listId = 101;
        long hasMovieId = 100000;

        UpdateWatchlistHasMovie updateHasMovie = new UpdateWatchlistHasMovie();
        updateHasMovie.setSeenAt(new Date());

        Mockito
                .when(watchlistService.exists(listId))
                .thenReturn(true);
        Mockito
                .when(watchlistMovieService.exists(hasMovieId))
                .thenReturn(false);

        MockHttpServletRequestBuilder reqBuilder = makeRequestBuilder(
                updateHasMovie,
                listId,
                hasMovieId
        );

        mockMvc.perform(reqBuilder)
                .andExpect(status()
                        .isNotFound());
    }

    private MockHttpServletRequestBuilder makeRequestBuilder(
            MoviePost moviePost,
            long listId) throws JsonProcessingException {
        MockHttpServletRequestBuilder reqBuilder = post(
                "/lists/{listId}/movies",
                listId
        );

        reqBuilder
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(moviePost));
        return reqBuilder;
    }

    private MockHttpServletRequestBuilder makeRequestBuilder(
            UpdateWatchlistHasMovie updateHasMovie,
            long listId,
            long hasMovieId) throws JsonProcessingException {
        MockHttpServletRequestBuilder reqBuilder = put(
                "/lists/{listId}/movies/{hasMovieId}",
                listId,
                hasMovieId
        );

        reqBuilder
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateHasMovie));
        return reqBuilder;
    }
}
