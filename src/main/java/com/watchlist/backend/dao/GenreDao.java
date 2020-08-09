package com.watchlist.backend.dao;

import com.watchlist.backend.model.Genre;
import org.springframework.data.repository.CrudRepository;

public interface GenreDao extends CrudRepository<Genre, Long> {
}
