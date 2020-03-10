package com.watchlist.backend.dao;

import com.watchlist.backend.model.Session;
import org.springframework.data.repository.CrudRepository;

public interface SessionDao extends CrudRepository<Session, Long> {
}
