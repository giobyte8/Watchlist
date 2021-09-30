package com.watchlist.backend.dao;

import com.watchlist.backend.entities.db.Watchlist;
import com.watchlist.backend.entities.db.WatchlistHasTVShow;
import org.springframework.data.repository.CrudRepository;

import java.util.Queue;

public interface WatchlistHasTVShowDao extends CrudRepository<WatchlistHasTVShow, Long> {

    Queue<WatchlistHasTVShow> findByWatchlistOrderByAddedAtDesc(Watchlist watchlist);
}
