package com.watchlist.backend.services;

import com.watchlist.backend.clients.FBClient;
import com.watchlist.backend.entities.db.AuthProvider;
import com.watchlist.backend.entities.json.UserCredentials;
import com.watchlist.backend.exceptions.WrongAuthProviderException;
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
            throw new WrongAuthProviderException(String.format(
                    "%s can't be used with auth provider id: %d",
                    getClass().getName(),
                    credentials.getAuthProviderId()
            ));
        }

        return fbClient.verifyToken(
                credentials.getEmail(),
                credentials.getToken()
        );
    }
}
