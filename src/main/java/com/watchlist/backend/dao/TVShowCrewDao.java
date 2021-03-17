package com.watchlist.backend.dao;

import com.watchlist.backend.entities.db.TVShowCrew;
import org.springframework.data.repository.CrudRepository;

public interface TVShowCrewDao extends CrudRepository<TVShowCrew, Long> {
}
