package com.watchlist.backend.services;

import com.watchlist.backend.clients.TmdbClient;
import com.watchlist.backend.dao.WatchlistHasMovieDao;
import com.watchlist.backend.entities.db.Language;
import com.watchlist.backend.entities.db.Movie;
import com.watchlist.backend.entities.db.User;
import com.watchlist.backend.entities.db.Watchlist;
import com.watchlist.backend.entities.db.WatchlistHasMovie;
import com.watchlist.backend.entities.json.WatchlistItemPost;
import com.watchlist.backend.exceptions.TmdbClientException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class ListMoviesServiceTest {

    @Mock
    private WatchlistHasMovieDao mockHasMovieDao;

    @Mock
    private EntityManager mockEntityMgr;

    @Mock
    private TmdbClient mockTmdbClient;

    @Mock
    private LocalizedMovieService mockLocMovieService;

    @Mock
    private LocalizedMediaService mockLocMediaService;

    @Mock
    private LanguageService mockLangService;

    @InjectMocks
    private ListMoviesService listMoviesService;


    @Test
    public void testAddMovie() throws Exception {
        long listId = 15432;
        long addedById = 34;
        int tmdbId = 4567;

        WatchlistItemPost itemPost = new WatchlistItemPost();
        itemPost.setTmdbId(tmdbId);
        itemPost.setLang(Language.ISO_EN_US);

        Movie movie = new Movie();
        movie.setTmdbId(tmdbId);

        User user = new User();
        user.setId(addedById);

        Watchlist list = new Watchlist();
        list.setId(listId);

        Language langEn = new Language();
        langEn.setIso639(Language.ISO_EN_US);

        Mockito
                .when(mockLangService.parseISO639(anyString()))
                .thenReturn(langEn);
        Mockito
                .when(mockTmdbClient.getMovie(
                        tmdbId,
                        langEn
                ))
                .thenReturn(movie);
        Mockito
                .when(mockLocMovieService.upsert(movie))
                .thenReturn(movie);
        Mockito
                .when(mockEntityMgr.getReference(User.class, addedById))
                .thenReturn(user);
        Mockito
                .when(mockEntityMgr.getReference(Watchlist.class, listId))
                .thenReturn(list);

        listMoviesService.addMovie(listId, addedById, itemPost);

        Mockito
                .verify(mockLocMediaService, times(1))
                .toWatchlistItem(
                        argThat((ArgumentMatcher<WatchlistHasMovie>) hasMovie ->
                                hasMovie.getWatchlist().equals(list) &&
                                hasMovie.getAddedBy().equals(user) &&
                                hasMovie.getMovie().equals(movie)),
                        argThat(language -> language
                                .getIso639()
                                .equals(Language.ISO_EN_US)
                        )
                );
    }

    @Test(expected = TmdbClientException.class)
    public void testAddMovie_TmdbError() throws IOException {
        long listId = 15432;
        long addedById = 34;
        int tmdbId = 87654;

        WatchlistItemPost itemPost = new WatchlistItemPost();
        itemPost.setTmdbId(tmdbId);
        itemPost.setLang(Language.ISO_EN_US);

        Language langEn = new Language();
        langEn.setIso639(Language.ISO_EN_US);

        Mockito
                .when(mockLangService.parseISO639(anyString()))
                .thenReturn(langEn);
        Mockito
                .when(mockTmdbClient.getMovie(tmdbId, langEn))
                .thenThrow(new IOException());

        listMoviesService.addMovie(listId, addedById, itemPost);
    }
}
