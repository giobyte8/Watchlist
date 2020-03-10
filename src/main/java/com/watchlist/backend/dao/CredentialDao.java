package com.watchlist.backend.dao;

import com.watchlist.backend.model.Credential;
import org.springframework.data.repository.CrudRepository;

public interface CredentialDao extends CrudRepository<Credential, Long> {
}
