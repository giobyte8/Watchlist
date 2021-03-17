package com.watchlist.backend.dao;

import com.watchlist.backend.entities.db.TVShow;
import org.springframework.data.repository.CrudRepository;

public interface TVShowDao extends CrudRepository<TVShow, Long> {

    boolean existsByTmdbId(int tmdbId);

    TVShow findByTmdbId(int tmdbId);
}
