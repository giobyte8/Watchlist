package com.watchlist.backend.services;

import com.watchlist.backend.clients.TmdbClient;
import com.watchlist.backend.dao.MovieDao;
import com.watchlist.backend.dao.WatchlistHasMovieDao;
import com.watchlist.backend.entities.MoviePost;
import com.watchlist.backend.entities.UpdateWatchlistHasMovie;
import com.watchlist.backend.exceptions.TmdbClientException;
import com.watchlist.backend.exceptions.WatchlistHasMovieNotFoundException;
import com.watchlist.backend.model.Movie;
import com.watchlist.backend.model.User;
import com.watchlist.backend.model.Watchlist;
import com.watchlist.backend.model.WatchlistHasMovie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.function.Supplier;

@Service
public class WatchlistMovieService {
    private final static Logger log = LoggerFactory
            .getLogger(WatchlistMovieService.class);

    private final WatchlistHasMovieDao hasMovieDao;
    private final MovieDao movieDao;
    private final EntityManager entityManager;
    private final TmdbClient tmdbClient;

    public WatchlistMovieService(WatchlistHasMovieDao hasMovieDao,
                                 MovieDao movieDao,
                                 EntityManager entityManager,
                                 TmdbClient tmdbClient) {
        this.hasMovieDao = hasMovieDao;
        this.movieDao = movieDao;
        this.entityManager = entityManager;
        this.tmdbClient = tmdbClient;
    }

    public Collection<WatchlistHasMovie> getMovies(long watchlistId) {
        Watchlist watchlist = entityManager.getReference(
                Watchlist.class,
                watchlistId
        );

        return hasMovieDao.findByWatchlist(watchlist);
    }

    public boolean exists(long watchlistHasMovieId) {
        return hasMovieDao.existsById(watchlistHasMovieId);
    }

    /**
     * Verifies if a movie is already part of a given watchlist
     * @param watchlistId List id
     * @param tmdbId Movie id from 'The movie database'
     * @return true if movie is already part of list
     */
    public boolean existsByWatchlistAndTmdbId(long watchlistId, int tmdbId) {
        return hasMovieDao.existsByWatchlistAndTmdbId(watchlistId, tmdbId);
    }

    /**
     * Adds a movie to a watchlist. If movie is not yet in our database
     * will be fetched along all its details from tmdb API
     * @param watchlistId List id
     * @param moviePost DTO object representing the movie being added
     * @return Created hasMovie object
     */
    @Transactional
    public WatchlistHasMovie addMovie(long watchlistId, MoviePost moviePost) {
        Movie movie;
        if (!movieDao.existsByTmdbId(moviePost.getTmdbId())) {
            try {
                movie = tmdbClient.getMovie(moviePost.getTmdbId());
                movieDao.save(movie);
            } catch (IOException e) {
                log.error("TMDBClient error", e);
                throw new TmdbClientException();
            }
        } else {
            movie = movieDao.findByTmdbId(moviePost.getTmdbId());
        }

        User user = entityManager.getReference(
                User.class,
                moviePost.getAddedBy()
        );
        Watchlist watchlist = entityManager.getReference(
                Watchlist.class,
                watchlistId
        );

        WatchlistHasMovie hasMovie = new WatchlistHasMovie();
        hasMovie.setWatchlist(watchlist);
        hasMovie.setAddedBy(user);
        hasMovie.setAddedAt(new Date());
        hasMovie.setMovie(movie);
        hasMovieDao.save(hasMovie);

        return hasMovie;
    }

    /**
     * Updates provided watchlist 'hasMovie' (Queried by id). Only
     * 'seen_at' will be updated for now
     *
     * @param hasMovie Watchlist has movie element to update
     * @return Updated element
     * @throws Throwable In case that provided has movie id does not exists
     */
    public WatchlistHasMovie updateHasMovie(UpdateWatchlistHasMovie hasMovie)
            throws Throwable {
        Supplier<Throwable> notFoundSupplier =
                WatchlistHasMovieNotFoundException::new;
        WatchlistHasMovie dbHasMovie = hasMovieDao
                .findById(hasMovie.getHasMovieId())
                .orElseThrow(notFoundSupplier);

        dbHasMovie.setSeenAt(hasMovie.getSeenAt());
        hasMovieDao.save(dbHasMovie);

        return dbHasMovie;
    }
}
