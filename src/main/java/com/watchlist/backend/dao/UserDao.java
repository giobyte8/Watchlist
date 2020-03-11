package com.watchlist.backend.dao;

import com.watchlist.backend.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserDao extends CrudRepository<User, Long> {

    User findByEmail(String email);
}
