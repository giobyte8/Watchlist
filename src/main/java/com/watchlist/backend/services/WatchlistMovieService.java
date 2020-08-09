package com.watchlist.backend.services;

import com.watchlist.backend.dao.WatchlistHasMovieDao;
import com.watchlist.backend.model.Watchlist;
import com.watchlist.backend.model.WatchlistHasMovie;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.Collection;

@Service
public class WatchlistMovieService {

    private final WatchlistHasMovieDao hasMovieDao;
    private final EntityManager entityManager;

    public WatchlistMovieService(WatchlistHasMovieDao hasMovieDao,
                                 EntityManager entityManager) {
        this.hasMovieDao = hasMovieDao;
        this.entityManager = entityManager;
    }

    public Collection<WatchlistHasMovie> getMovies(long watchlistId) {
        Watchlist watchlist = entityManager.getReference(
                Watchlist.class,
                watchlistId
        );

        return hasMovieDao.findByWatchlist(watchlist);
    }
}
