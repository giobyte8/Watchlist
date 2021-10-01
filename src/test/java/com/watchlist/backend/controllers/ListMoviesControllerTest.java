package com.watchlist.backend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.watchlist.backend.TestSecurityConfig;
import com.watchlist.backend.entities.UpdateWatchlistHasMovie;
import com.watchlist.backend.entities.db.Language;
import com.watchlist.backend.entities.db.WatchlistHasMovie;
import com.watchlist.backend.entities.json.LocalizedListItem;
import com.watchlist.backend.entities.json.WatchlistItemPost;
import com.watchlist.backend.services.ListMoviesService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ListMoviesController.class)
@Import(TestSecurityConfig.class)
@MockBean({ RestTemplate.class })
public class ListMoviesControllerTest extends WithAuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ListMoviesService listMoviesService;

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
                .when(listMoviesService.getMovies(watchlistId))
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
        long userId = 1;
        long listId = 1;
        int tmdbId = 104;

        WatchlistItemPost moviePost = new WatchlistItemPost();
        moviePost.setLang(Language.ISO_ES_MX);
        moviePost.setTmdbId(tmdbId);

        LocalizedListItem listItem = new LocalizedListItem();
        listItem.setTmdbId(tmdbId);

        Mockito
                .when(watchlistService.exists(listId))
                .thenReturn(true);
        Mockito
                .when(listMoviesService.existsByWatchlistAndTmdbId(
                        listId,
                        tmdbId
                ))
                .thenReturn(false);
        Mockito
                .when(listMoviesService.addMovie(
                        anyLong(),
                        anyLong(),
                        any(WatchlistItemPost.class)
                ))
                .thenReturn(listItem);

        String reqBody = mapper.writeValueAsString(moviePost);
        MvcResult mvcResult = mockMvc
                .perform(authenticatedPostBuilder(
                            userId,
                            "/lists/{listId}/movies",
                            listId
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tmdb_id").isNumber())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        LocalizedListItem localizedListItem = mapper.readValue(
                response,
                LocalizedListItem.class
        );
        assertEquals(tmdbId, localizedListItem.getTmdbId());
    }

    @Test
    public void testAddMovieWithoutValues() throws Exception {

        // Post without 'tmdb_id' in req body
        mockMvc.perform(makeReqBuilder(new WatchlistItemPost(), 1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    public void testAddMovieToNonExistentList() throws Exception {
        long listId = 501;
        int tmdbId = 105;

        WatchlistItemPost moviePost = new WatchlistItemPost();
        moviePost.setTmdbId(tmdbId);

        Mockito
                .when(watchlistService.exists(listId))
                .thenReturn(false);

        mockMvc
                .perform(makeReqBuilder(moviePost, listId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAddDuplicatedMovie() throws Exception {
        long listId = 501;
        int tmdbId = 105;

        WatchlistItemPost moviePost = new WatchlistItemPost();
        moviePost.setTmdbId(tmdbId);

        Mockito
                .when(watchlistService.exists(listId))
                .thenReturn(true);
        Mockito
                .when(listMoviesService.existsByWatchlistAndTmdbId(
                        listId,
                        tmdbId
                ))
                .thenReturn(true);

        mockMvc
                .perform(makeReqBuilder(moviePost, listId))
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
                .when(listMoviesService.exists(hasMovieId))
                .thenReturn(true);
        Mockito
                .when(listMoviesService.updateHasMovie(
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
                .when(listMoviesService.exists(hasMovieId))
                .thenReturn(false);

        MockHttpServletRequestBuilder reqBuilder = makeRequestBuilder(
                updateHasMovie,
                listId,
                hasMovieId
        );

        mockMvc
                .perform(reqBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteWatchlistHasMovie() throws Throwable {
        long listId = 101;
        long hasMovieId = 100000;

        Mockito
                .when(watchlistService.exists(listId))
                .thenReturn(true);
        Mockito
                .when(listMoviesService.exists(hasMovieId))
                .thenReturn(true);

        MockHttpServletRequestBuilder reqBuilder = makeRequestBuilder(
                listId,
                hasMovieId
        );

        mockMvc
                .perform(reqBuilder)
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteHasMovieNonExistentList() throws Throwable {
        long listId = 101;
        long hasMovieId = 100000;

        Mockito
                .when(watchlistService.exists(listId))
                .thenReturn(false);

        MockHttpServletRequestBuilder reqBuilder = makeRequestBuilder(
                listId,
                hasMovieId
        );

        mockMvc
                .perform(reqBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteNonExistentWatchlistHasMovie() throws Throwable {
        long listId = 101;
        long hasMovieId = 100000;

        Mockito
                .when(watchlistService.exists(listId))
                .thenReturn(true);
        Mockito
                .when(listMoviesService.exists(hasMovieId))
                .thenReturn(false);

        MockHttpServletRequestBuilder reqBuilder = makeRequestBuilder(
                listId,
                hasMovieId
        );

        mockMvc
                .perform(reqBuilder)
                .andExpect(status().isNotFound());
    }

    private MockHttpServletRequestBuilder makeReqBuilder(
            WatchlistItemPost moviePost,
            long listId) throws JsonProcessingException {
        return authenticatedPostBuilder("/lists/{listId}/movies", listId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(moviePost));
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

    private MockHttpServletRequestBuilder makeRequestBuilder(
            long listId,
            long hasMovieId) {
        return delete(
                "/lists/{listId}/movies/{hasMovieId}",
                listId,
                hasMovieId
        );
    }
}
