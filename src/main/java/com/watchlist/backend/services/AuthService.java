package com.watchlist.backend.services;

import com.watchlist.backend.entities.UserCredentials;

public interface AuthService {

    boolean authenticate(UserCredentials credentials);
}
