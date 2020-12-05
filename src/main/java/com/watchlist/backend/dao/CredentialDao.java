package com.watchlist.backend.dao;

import com.watchlist.backend.entities.db.AuthProvider;
import com.watchlist.backend.entities.db.Credential;
import com.watchlist.backend.entities.db.User;
import org.springframework.data.repository.CrudRepository;

public interface CredentialDao extends CrudRepository<Credential, Long> {

    Credential findByAuthProviderAndUser(AuthProvider authProvider, User user);
}
