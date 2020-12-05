package com.watchlist.backend.services;

import com.watchlist.backend.entities.json.UserCredentials;

public interface AuthService {

    boolean authenticate(UserCredentials credentials);
}
