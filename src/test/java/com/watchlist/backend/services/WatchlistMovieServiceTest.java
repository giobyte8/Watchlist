package com.watchlist.backend.services;

import com.watchlist.backend.clients.TmdbClient;
import com.watchlist.backend.dao.MovieDao;
import com.watchlist.backend.dao.WatchlistHasMovieDao;
import com.watchlist.backend.entities.MoviePost;
import com.watchlist.backend.entities.UpdateWatchlistHasMovie;
import com.watchlist.backend.exceptions.WatchlistHasMovieNotFoundException;
import com.watchlist.backend.model.Movie;
import com.watchlist.backend.model.WatchlistHasMovie;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class WatchlistMovieServiceTest {

    @Mock
    private WatchlistHasMovieDao hasMovieDao;

    @Mock
    private MovieDao movieDao;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TmdbClient tmdbClient;

    @InjectMocks
    private WatchlistMovieService watchlistMovieService;

    @Test
    public void testAddNonExistentMovie() throws IOException {
        int tmdbId = 100001;

        Movie movie = new Movie();
        movie.setTitle("Heroes");

        MoviePost moviePost = new MoviePost();
        moviePost.setTmdbId(tmdbId);
        moviePost.setAddedBy(1);

        Mockito
                .when(movieDao.existsByTmdbId(tmdbId))
                .thenReturn(false);
        Mockito
                .when(tmdbClient.getMovie(tmdbId))
                .thenReturn(movie);

        watchlistMovieService.addMovie(101, moviePost);

        Mockito
                .verify(movieDao, times(1))
                .save(movie);
        Mockito
                .verify(hasMovieDao, times(1))
                .save(argThat(hasMovie -> hasMovie.getMovie().equals(movie)));
    }

    @Test
    public void testAddAlreadyExistentMovie() {
        int tmdbId = 100001;

        Movie movie = new Movie();
        movie.setTitle("Heroes");

        MoviePost moviePost = new MoviePost();
        moviePost.setTmdbId(tmdbId);
        moviePost.setAddedBy(1);

        Mockito
                .when(movieDao.existsByTmdbId(tmdbId))
                .thenReturn(true);
        Mockito
                .when(movieDao.findByTmdbId(tmdbId))
                .thenReturn(movie);

        watchlistMovieService.addMovie(101, moviePost);

        Mockito
                .verify(movieDao, never())
                .save(movie);
        Mockito
                .verify(hasMovieDao, times(1))
                .save(argThat(hasMovie -> hasMovie.getMovie().equals(movie)));
    }

    @Test
    public void testUpdateWatchlistHasMovie() throws Throwable {
        long hasMovieId = 1003;
        Date seenAt = new Date();

        UpdateWatchlistHasMovie updateHasMovie = new UpdateWatchlistHasMovie();
        updateHasMovie.setHasMovieId(hasMovieId);
        updateHasMovie.setSeenAt(seenAt);

        WatchlistHasMovie dbHasMovie = new WatchlistHasMovie();
        dbHasMovie.setId(hasMovieId);

        Mockito
                .when(hasMovieDao.findById(hasMovieId))
                .thenReturn(Optional.of(dbHasMovie));

        WatchlistHasMovie rHasMovie = watchlistMovieService
                .updateHasMovie(updateHasMovie);

        assertEquals(updateHasMovie.getHasMovieId(), rHasMovie.getId());
        assertEquals(updateHasMovie.getSeenAt(), rHasMovie.getSeenAt());
    }

    @Test(expected = WatchlistHasMovieNotFoundException.class)
    public void testUpdateNonExistentWatchlistHasMovie() throws Throwable {
        long hasMovieId = 1003;
        Date seenAt = new Date();

        UpdateWatchlistHasMovie updateHasMovie = new UpdateWatchlistHasMovie();
        updateHasMovie.setHasMovieId(hasMovieId);
        updateHasMovie.setSeenAt(seenAt);

        Mockito
                .when(hasMovieDao.findById(hasMovieId))
                .thenReturn(Optional.empty());

        watchlistMovieService.updateHasMovie(updateHasMovie);

        // Then exception should be thrown
    }
}
