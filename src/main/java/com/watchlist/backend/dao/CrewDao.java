package com.watchlist.backend.dao;

import com.watchlist.backend.entities.db.MovieCrew;
import org.springframework.data.repository.CrudRepository;

public interface CrewDao extends CrudRepository<MovieCrew, Long> {
}
