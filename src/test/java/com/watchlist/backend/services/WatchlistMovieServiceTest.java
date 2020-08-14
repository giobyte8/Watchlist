package com.watchlist.backend.services;

import com.watchlist.backend.clients.TmdbClient;
import com.watchlist.backend.dao.MovieDao;
import com.watchlist.backend.dao.WatchlistHasMovieDao;
import com.watchlist.backend.entities.MoviePost;
import com.watchlist.backend.model.Movie;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import java.io.IOException;

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
}
