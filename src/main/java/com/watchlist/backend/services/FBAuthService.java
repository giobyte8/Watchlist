package com.watchlist.backend.services;

import com.watchlist.backend.clients.FBClient;
import com.watchlist.backend.entities.UserCredentials;
import com.watchlist.backend.model.AuthProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FBAuthService implements AuthService {

    private final FBClient fbClient;

    public FBAuthService(FBClient fbClient) {
        this.fbClient = fbClient;
    }

    @Transactional
    @Override
    public boolean authenticate(UserCredentials credentials) {
        if (credentials.getAuthProviderId() != AuthProvider.FB_AUTH_PROVIDER) {
            // TODO Launch custom unsupported exception
            return false;
        }

        return fbClient.verifyToken(
                credentials.getEmail(),
                credentials.getToken()
        );
    }
}
