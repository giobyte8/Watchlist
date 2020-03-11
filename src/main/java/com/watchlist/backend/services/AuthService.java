package com.watchlist.backend.services;

import com.watchlist.backend.entities.LoginResponse;
import com.watchlist.backend.entities.UserCredentials;

public interface AuthService {

    LoginResponse login(UserCredentials credentials);
}
