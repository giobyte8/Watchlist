package com.watchlist.backend.dao;

import com.watchlist.backend.model.AuthProvider;
import com.watchlist.backend.model.Credential;
import com.watchlist.backend.model.User;
import org.springframework.data.repository.CrudRepository;

public interface CredentialDao extends CrudRepository<Credential, Long> {

    Credential findByAuthProviderAndUser(AuthProvider authProvider, User user);
}
