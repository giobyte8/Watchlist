package com.watchlist.backend.dao;

import com.watchlist.backend.model.Crew;
import org.springframework.data.repository.CrudRepository;

public interface CrewDao extends CrudRepository<Crew, Long> {
}
