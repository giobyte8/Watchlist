package com.watchlist.backend.dao;

import com.watchlist.backend.model.Movie;
import org.springframework.data.repository.CrudRepository;

public interface MovieDao extends CrudRepository<Movie, Long> {
}
