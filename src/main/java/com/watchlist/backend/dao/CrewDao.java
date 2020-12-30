package com.watchlist.backend.dao;

import com.watchlist.backend.entities.db.Crew;
import org.springframework.data.repository.CrudRepository;

public interface CrewDao extends CrudRepository<Crew, Long> {
}
