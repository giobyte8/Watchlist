package com.watchlist.backend.dao;

import com.watchlist.backend.entities.db.Watchlist;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface WatchlistDao extends CrudRepository<Watchlist, Long> {

    /**
     * Verifies if a watchlist with given name an under
     * ownership of indicated user already exists
     *
     * @param name Watchlist name
     * @param ownerId Id of list owner
     * @return true if a list already exists
     */
    @Query(value = "SELECT (count(w) > 0) " +
            "FROM UserHasWatchlist uhw " +
            "INNER JOIN uhw.watchlist w " +
            "WHERE uhw.user.id = :ownerId AND w.name = :name")
    boolean existsByNameAndOwner(@Param("name") String name,
                                 @Param("ownerId") long ownerId);
}
