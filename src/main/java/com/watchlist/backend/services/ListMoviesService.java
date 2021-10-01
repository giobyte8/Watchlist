package com.watchlist.backend.services;

import com.watchlist.backend.clients.TmdbClient;
import com.watchlist.backend.dao.WatchlistHasMovieDao;
import com.watchlist.backend.entities.UpdateWatchlistHasMovie;
import com.watchlist.backend.entities.db.Language;
import com.watchlist.backend.entities.db.Movie;
import com.watchlist.backend.entities.db.User;
import com.watchlist.backend.entities.db.Watchlist;
import com.watchlist.backend.entities.db.WatchlistHasMovie;
import com.watchlist.backend.entities.json.LocalizedListHasMovie;
import com.watchlist.backend.entities.json.LocalizedListItem;
import com.watchlist.backend.entities.json.WatchlistItemPost;
import com.watchlist.backend.exceptions.TmdbClientException;
import com.watchlist.backend.exceptions.ListHasItemNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.function.Supplier;

@Service
public class ListMoviesService {
    private final static Logger log = LoggerFactory
            .getLogger(ListMoviesService.class);

    private final WatchlistHasMovieDao hasMovieDao;
    private final EntityManager entityManager;
    private final TmdbClient tmdbClient;

    private final LocalizedMovieService locMovieService;
    private final LocalizedMediaService locMediaService;
    private final LanguageService langService;

    public ListMoviesService(WatchlistHasMovieDao hasMovieDao,
                             EntityManager entityManager,
                             TmdbClient tmdbClient,
                             LocalizedMovieService locMovieService,
                             LocalizedMediaService locMediaService,
                             LanguageService langService) {
        this.hasMovieDao = hasMovieDao;
        this.entityManager = entityManager;
        this.tmdbClient = tmdbClient;
        this.locMovieService = locMovieService;
        this.locMediaService = locMediaService;
        this.langService = langService;
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
     * Adds a movie to watchlist. Movie will be fetched from tmdb even if
     * is already in our db (Information will be updated)
     *
     * Movie will be fetched in all supported languages so that it be
     * available for other languages in future
     *
     * @param watchlistId List id
     * @param addedById Id of user who is adding movie to list
     * @param moviePost DTO object representing the movie being added
     * @return Created list content item
     */
    @Transactional
    public LocalizedListItem addMovie(long watchlistId,
                                      long addedById,
                                      WatchlistItemPost moviePost) {
        Movie dbMovie = null;

        // Fetch and insert localizations for movie
        for (String langISO639 : Language.SUPPORTED_LANGUAGES) {
            try {
                Movie movie = tmdbClient.getMovie(
                        moviePost.getTmdbId(),
                        langService.parseISO639(langISO639)
                );

                dbMovie = locMovieService.upsert(movie);
            } catch (IOException e) {
                log.error("TMDBClient error", e);
                throw new TmdbClientException();
            }

        }

        if (Objects.isNull(dbMovie)) {
            log.error("Movie was not saved");
            throw new TmdbClientException();
        }

        User user = entityManager.getReference(
                User.class,
                addedById
        );
        Watchlist watchlist = entityManager.getReference(
                Watchlist.class,
                watchlistId
        );

        WatchlistHasMovie hasMovie = new WatchlistHasMovie();
        hasMovie.setWatchlist(watchlist);
        hasMovie.setAddedBy(user);
        hasMovie.setMovie(dbMovie);
        hasMovie.setAddedAt(new Date());
        hasMovieDao.save(hasMovie);

        // Return movie in language that client sent it
        return locMediaService.toWatchlistItem(
                hasMovie,
                langService.parseISO639(moviePost.getLang())
        );
    }

    public LocalizedListHasMovie getMovie(long listId, int tmdbId, String lang) {
        WatchlistHasMovie hasMovie = hasMovieDao.findByWatchlistAndTmdbId(
                listId,
                tmdbId
        );

        return locMediaService.toLocalizedHasMovie(
                hasMovie,
                langService.parseISO639(lang)
        );
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
                ListHasItemNotFoundException::new;
        WatchlistHasMovie dbHasMovie = hasMovieDao
                .findById(hasMovie.getHasMovieId())
                .orElseThrow(notFoundSupplier);

        dbHasMovie.setSeenAt(hasMovie.getSeenAt());
        hasMovieDao.save(dbHasMovie);

        return dbHasMovie;
    }

    public void deleteHasMovie(long hasMovieId) {
        hasMovieDao.deleteById(hasMovieId);
    }
}
