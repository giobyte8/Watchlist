package com.watchlist.backend.dao;

import com.watchlist.backend.model.Cast;
import org.springframework.data.repository.CrudRepository;

public interface CastDao extends CrudRepository<Cast, Long> {
}
