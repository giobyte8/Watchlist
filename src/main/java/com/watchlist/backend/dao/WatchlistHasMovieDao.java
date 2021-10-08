package com.watchlist.backend.dao;

import com.watchlist.backend.entities.db.Watchlist;
import com.watchlist.backend.entities.db.WatchlistHasMovie;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Queue;

public interface WatchlistHasMovieDao
        extends CrudRepository<WatchlistHasMovie, Long> {

    Queue<WatchlistHasMovie> findByWatchlistOrderByAddedAtDesc(Watchlist watchlist);

    @Query(value = "SELECT (count(whm) > 0) " +
            "FROM WatchlistHasMovie whm " +
            "WHERE whm.watchlist.id = :watchlistId " +
            "  AND whm.movie.tmdbId = :tmdbId")
    boolean existsByWatchlistAndTmdbId(@Param("watchlistId") long watchlistId,
                                       @Param("tmdbId") int tmdbId);

    @Query(value = "SELECT whm " +
            "FROM WatchlistHasMovie whm " +
            "WHERE whm.watchlist.id = :watchlistId " +
            "  AND whm.movie.tmdbId = :tmdbId")
    Optional<WatchlistHasMovie> findByWatchlistAndTmdbId(
            @Param("watchlistId") long watchlistId,
            @Param("tmdbId") int tmdbId
    );
}
