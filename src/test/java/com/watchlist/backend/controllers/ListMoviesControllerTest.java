package com.watchlist.backend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.watchlist.backend.TestSecurityConfig;
import com.watchlist.backend.entities.db.Language;
import com.watchlist.backend.entities.json.LocalizedListHasMovie;
import com.watchlist.backend.entities.json.LocalizedListItem;
import com.watchlist.backend.entities.json.WatchlistItemPatch;
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
import org.springframework.web.client.RestTemplate;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
    private ListMoviesService mockListMoviesService;

    @MockBean
    private WatchlistService mockWatchlistService;

    @Test
    public void testAddMovie() throws Exception {
        long listId = 1;
        int tmdbId = 104;

        WatchlistItemPost moviePost = new WatchlistItemPost();
        moviePost.setLang(Language.ISO_ES_MX);
        moviePost.setTmdbId(tmdbId);

        LocalizedListItem listItem = new LocalizedListItem();
        listItem.setTmdbId(tmdbId);

        Mockito
                .when(mockWatchlistService.exists(listId))
                .thenReturn(true);
        Mockito
                .when(mockListMoviesService.existsByWatchlistAndTmdbId(
                        listId,
                        tmdbId
                ))
                .thenReturn(false);
        Mockito
                .when(mockListMoviesService.addMovie(
                        anyLong(),
                        anyLong(),
                        any(WatchlistItemPost.class)
                ))
                .thenReturn(listItem);

        String reqBody = mapper.writeValueAsString(moviePost);
        MvcResult mvcResult = mockMvc
                .perform(authPostBuilder(
                        MediaType.APPLICATION_JSON,
                        reqBody,
                        "/lists/{listId}/movies",
                        listId
                ))
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
    public void testAddMovie_InvalidPayload() throws Exception {

        // Post without 'tmdb_id' in req body
        mockMvc.perform(makeReqBuilder(new WatchlistItemPost(), 1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    public void testAddMovie_NonExistentList() throws Exception  {
        long listId = 501;
        int tmdbId = 105;

        WatchlistItemPost moviePost = new WatchlistItemPost();
        moviePost.setTmdbId(tmdbId);

        String reqBody = mapper.writeValueAsString(moviePost);
        Mockito
                .when(mockWatchlistService.exists(listId))
                .thenReturn(false);

        mockMvc
                .perform(authPostBuilder(
                        MediaType.APPLICATION_JSON,
                        reqBody,
                        "/lists/{listId}/movies",
                        listId
                ))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAddMovie_ExistentMovie() throws Exception {
        long listId = 501;
        int tmdbId = 105;

        WatchlistItemPost moviePost = new WatchlistItemPost();
        moviePost.setTmdbId(tmdbId);

        Mockito
                .when(mockWatchlistService.exists(listId))
                .thenReturn(true);
        Mockito
                .when(mockListMoviesService.existsByWatchlistAndTmdbId(
                        listId,
                        tmdbId
                ))
                .thenReturn(true);

        String reqBody = mapper.writeValueAsString(moviePost);
        mockMvc
                .perform(authPostBuilder(
                        MediaType.APPLICATION_JSON,
                        reqBody,
                        "/lists/{listId}/movies",
                        listId
                ))
                .andExpect(status().isConflict());
    }

    @Test
    public void testGetListHasMovie() throws Throwable {
        long hasMovieId = 5000;
        long watchlistId = 101;
        int tmdbId = 123;

        LocalizedListHasMovie hasMovie = new LocalizedListHasMovie();
        hasMovie.setId(hasMovieId);


        Mockito
                .when(mockWatchlistService.exists(watchlistId))
                .thenReturn(true);
        Mockito
                .when(mockListMoviesService.existsByWatchlistAndTmdbId(
                        watchlistId,
                        tmdbId
                ))
                .thenReturn(true);
        Mockito
                .when(mockListMoviesService.getMovie(
                        watchlistId,
                        tmdbId,
                        Language.ISO_EN_US
                ))
                .thenReturn(hasMovie);

        MockHttpServletRequestBuilder reqBuilder = get(
                "/lists/{listId}/movies/{tmdbId}",
                watchlistId,
                tmdbId
        );
        MvcResult mvcResult = mockMvc
                .perform(reqBuilder)
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        LocalizedListHasMovie locHasMovie = mapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                LocalizedListHasMovie.class
        );

        assertEquals(hasMovieId, locHasMovie.getId());
    }

    @Test
    public void testGetListHasMovie_NonExistentList() throws Exception {
        long listId = 501;
        int tmdbId = 1;

        Mockito
                .when(mockWatchlistService.exists(listId))
                .thenReturn(false);

        mockMvc
                .perform(get(
                        "/lists/{listId}/movies/{tmdbId}",
                        listId,
                        tmdbId
                ))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetListHasMovie_NonExistentMovie() throws Exception {
        long listId = 501;
        int tmdbId = 1;

        Mockito
                .when(mockWatchlistService.exists(listId))
                .thenReturn(true);
        Mockito
                .when(mockListMoviesService.existsByWatchlistAndTmdbId(
                        listId,
                        tmdbId
                ))
                .thenReturn(false);

        mockMvc
                .perform(get(
                        "/lists/{listId}/movies/{tmdbId}",
                        listId,
                        tmdbId
                ))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testPatchListHasMovie() throws Throwable {
        long listId = 101;
        int tmdbId = 1000;

        WatchlistItemPatch hasMoviePatch = new WatchlistItemPatch();
        hasMoviePatch.setSeenAt(new Date());

        Mockito
                .when(mockWatchlistService.exists(listId))
                .thenReturn(true);
        Mockito
                .when(mockListMoviesService.existsByWatchlistAndTmdbId(
                        listId,
                        tmdbId
                ))
                .thenReturn(true);

        MockHttpServletRequestBuilder reqBuilder = makeRequestBuilder(
                listId,
                tmdbId,
                hasMoviePatch
        );
        mockMvc.perform(reqBuilder)
                .andExpect(status().is2xxSuccessful());

        Mockito
                .verify(mockListMoviesService, times(1))
                .updateHasMovie(
                        anyLong(),
                        anyInt(),
                        any(WatchlistItemPatch.class)
                );
    }

    @Test
    public void testPatchListHasMovie_NonExistingList() throws Throwable {
        long listId = 101;
        int tmdbId = 100000;

        WatchlistItemPatch hasMoviePatch = new WatchlistItemPatch();
        hasMoviePatch.setSeenAt(new Date());

        Mockito
                .when(mockWatchlistService.exists(listId))
                .thenReturn(false);

        MockHttpServletRequestBuilder reqBuilder = makeRequestBuilder(
                listId,
                tmdbId,
                hasMoviePatch
        );

        mockMvc.perform(reqBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testPatchListHasMovie_NonExistentMovie() throws Throwable {
        long listId = 101;
        int tmdbId = 100000;

        WatchlistItemPatch hasMoviePatch = new WatchlistItemPatch();
        hasMoviePatch.setSeenAt(new Date());

        Mockito
                .when(mockWatchlistService.exists(listId))
                .thenReturn(false);
        Mockito
                .when(mockListMoviesService.existsByWatchlistAndTmdbId(
                        listId,
                        tmdbId
                ))
                .thenReturn(false);

        MockHttpServletRequestBuilder reqBuilder = makeRequestBuilder(
                listId,
                tmdbId,
                hasMoviePatch
        );

        mockMvc.perform(reqBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteWatchlistHasMovie() throws Throwable {
        long listId = 1001;
        int tmdbId = 100;

        Mockito
                .when(mockWatchlistService.exists(listId))
                .thenReturn(true);
        Mockito
                .when(mockListMoviesService.existsByWatchlistAndTmdbId(
                        listId,
                        tmdbId
                ))
                .thenReturn(true);

        MockHttpServletRequestBuilder reqBuilder = makeRequestBuilder(
                listId,
                tmdbId
        );

        mockMvc
                .perform(reqBuilder)
                .andExpect(status().isNoContent());

        Mockito
                .verify(mockListMoviesService, times(1))
                .deleteHasMovie(listId, tmdbId);
    }

    @Test
    public void testDeleteWatchlistHasMovie_NonExistentList() throws Throwable {
        long listId = 101;
        int tmdbId = 10000;

        Mockito
                .when(mockWatchlistService.exists(listId))
                .thenReturn(false);

        MockHttpServletRequestBuilder reqBuilder = makeRequestBuilder(
                listId,
                tmdbId
        );

        mockMvc
                .perform(reqBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteWatchlistHasMovie_NonExistentMovie() throws Throwable {
        long listId = 101;
        int tmdbId = 10000;

        Mockito
                .when(mockWatchlistService.exists(listId))
                .thenReturn(true);
        Mockito
                .when(mockListMoviesService.exists(tmdbId))
                .thenReturn(false);

        MockHttpServletRequestBuilder reqBuilder = makeRequestBuilder(
                listId,
                tmdbId
        );

        mockMvc
                .perform(reqBuilder)
                .andExpect(status().isNotFound());
    }

    private MockHttpServletRequestBuilder makeReqBuilder(
            WatchlistItemPost moviePost,
            long listId) throws JsonProcessingException {
        return authPostBuilder("/lists/{listId}/movies", listId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(moviePost));
    }

    private MockHttpServletRequestBuilder makeRequestBuilder(
            long listId,
            int tmdbId,
            WatchlistItemPatch hasMoviePatch) throws JsonProcessingException {
        MockHttpServletRequestBuilder reqBuilder = patch(
                "/lists/{listId}/movies/{tmdbId}",
                listId,
                tmdbId
        );

        reqBuilder
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(hasMoviePatch));
        return reqBuilder;
    }

    private MockHttpServletRequestBuilder makeRequestBuilder(
            long listId,
            int tmdbId) {
        return delete(
                "/lists/{listId}/movies/{tmdbId}",
                listId,
                tmdbId
        );
    }
}
