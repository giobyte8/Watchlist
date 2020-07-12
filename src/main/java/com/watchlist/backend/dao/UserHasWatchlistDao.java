package com.watchlist.backend.dao;

import com.watchlist.backend.model.UserHasWatchlist;
import org.springframework.data.repository.CrudRepository;

public interface UserHasWatchlistDao extends CrudRepository<UserHasWatchlist, Long> {
}
