package com.watchlist.backend.dao;

import com.watchlist.backend.entities.db.Session;
import org.springframework.data.repository.CrudRepository;

public interface SessionDao extends CrudRepository<Session, Long> {
}
