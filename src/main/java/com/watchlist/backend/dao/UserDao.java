package com.watchlist.backend.dao;

import com.watchlist.backend.entities.db.User;
import org.springframework.data.repository.CrudRepository;

public interface UserDao extends CrudRepository<User, Long> {

    User findByEmail(String email);
}
