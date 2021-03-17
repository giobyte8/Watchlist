package com.watchlist.backend.dao;

import com.watchlist.backend.entities.db.TVShowCast;
import org.springframework.data.repository.CrudRepository;

public interface TVShowCastDao extends CrudRepository<TVShowCast, Long> {
}
