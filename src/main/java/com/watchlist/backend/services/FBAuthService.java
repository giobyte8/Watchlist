package com.watchlist.backend.services;

import com.watchlist.backend.clients.FBClient;
import com.watchlist.backend.dao.CredentialDao;
import com.watchlist.backend.dao.UserDao;
import com.watchlist.backend.entities.LoginResponse;
import com.watchlist.backend.entities.UserCredentials;
import com.watchlist.backend.model.AuthProvider;
import com.watchlist.backend.model.Credential;
import com.watchlist.backend.model.Role;
import com.watchlist.backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;

@Service("FBAuthService")
public class FBAuthService implements AuthService {

    private final FBClient fbClient;
    private final EntityManager entityManager;
    private final UserDao userDao;
    private final CredentialDao credentialDao;

    public FBAuthService(FBClient fbClient, UserDao userDao,
                         EntityManager entityManager, CredentialDao credentialDao) {
        this.fbClient = fbClient;
        this.userDao = userDao;
        this.entityManager = entityManager;
        this.credentialDao = credentialDao;
    }

    @Override
    public LoginResponse login(UserCredentials fbCredentials) {
        LoginResponse loginResponse = new LoginResponse();

        if (fbClient.verifyToken(fbCredentials.getEmail(), fbCredentials.getToken())) {

            //
            // Persist user
            User user = userDao.findByEmail(fbCredentials.getEmail());
            if (user == null) {
                Role watcherRole = entityManager
                        .getReference(Role.class, Role.WATCHER);
                user = new User();
                user.setRole(watcherRole);
            }
            user.setName(fbCredentials.getName());
            user.setEmail(fbCredentials.getEmail());
            user.setPicture(fbCredentials.getPicture());
            userDao.save(user);

            //
            // Persist credential
            AuthProvider fbAuthProv = entityManager.getReference(
                    AuthProvider.class,
                    AuthProvider.FB_AUTH_PROVIDER);
            Credential watchlistCredential = credentialDao
                    .findByAuthProviderAndUser(fbAuthProv, user);
            if (watchlistCredential == null) {
                watchlistCredential = new Credential();
                watchlistCredential.setUser(user);
                watchlistCredential.setAuthProvider(fbAuthProv);
            }
            watchlistCredential.setToken(fbCredentials.getToken());
            credentialDao.save(watchlistCredential);

            // TODO Create jwt and persist session started

            loginResponse.setSuccess(true);
            loginResponse.setMessage("Login successful");
        } else {
            loginResponse.setMessage("Provided token is invalid");
        }

        return loginResponse;
    }
}
