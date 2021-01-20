package com.watchlist.backend.dao;

import com.watchlist.backend.entities.db.MovieGenre;
import org.springframework.data.repository.CrudRepository;

public interface GenreDao extends CrudRepository<MovieGenre, Long> {

//    boolean existsByName(String name);
}
