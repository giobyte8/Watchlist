package com.watchlist.backend.dao;

import com.watchlist.backend.model.Watchlist;
import org.springframework.data.repository.CrudRepository;

public interface WatchlistDao extends CrudRepository<Watchlist, Long> {
}
