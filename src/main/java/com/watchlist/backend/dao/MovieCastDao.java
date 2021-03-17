package com.watchlist.backend.dao;

import com.watchlist.backend.entities.db.MovieCast;
import org.springframework.data.repository.CrudRepository;

public interface MovieCastDao extends CrudRepository<MovieCast, Long> {
}
