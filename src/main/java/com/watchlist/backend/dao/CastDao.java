package com.watchlist.backend.dao;

import com.watchlist.backend.entities.db.Cast;
import org.springframework.data.repository.CrudRepository;

public interface CastDao extends CrudRepository<Cast, Long> {
}
