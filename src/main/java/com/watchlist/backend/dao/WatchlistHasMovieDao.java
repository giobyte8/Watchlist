package com.watchlist.backend.dao;

import com.watchlist.backend.model.Watchlist;
import com.watchlist.backend.model.WatchlistHasMovie;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface WatchlistHasMovieDao
        extends CrudRepository<WatchlistHasMovie, Long> {

    Collection<WatchlistHasMovie> findByWatchlist(Watchlist watchlist);

    @Query(value = "SELECT (count(whm) > 0) " +
            "FROM WatchlistHasMovie whm " +
            "WHERE whm.watchlist.id = :watchlistId " +
            "  AND whm.movie.tmdbId = :tmdbId")
    boolean existsByWatchlistAndTmdbId(@Param("watchlistId") long watchlistId,
                                       @Param("tmdbId") int tmdbId);
}
