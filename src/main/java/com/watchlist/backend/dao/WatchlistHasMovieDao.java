package com.watchlist.backend.dao;

import com.watchlist.backend.model.Watchlist;
import com.watchlist.backend.model.WatchlistHasMovie;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface WatchlistHasMovieDao
        extends CrudRepository<WatchlistHasMovie, Long> {

    Collection<WatchlistHasMovie> findByWatchlist(Watchlist watchlist);
}
