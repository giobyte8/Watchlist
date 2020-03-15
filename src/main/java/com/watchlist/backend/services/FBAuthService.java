package com.watchlist.backend.services;

import com.watchlist.backend.clients.FBClient;
import com.watchlist.backend.dao.CredentialDao;
import com.watchlist.backend.dao.SessionDao;
import com.watchlist.backend.dao.UserDao;
import com.watchlist.backend.entities.LoginResponse;
import com.watchlist.backend.entities.UserCredentials;
import com.watchlist.backend.model.*;
import com.watchlist.backend.security.JWTUtils;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;

@Service("FBAuthService")
public class FBAuthService implements AuthService {

    private final FBClient fbClient;
    private final EntityManager entityManager;
    private final UserDao userDao;
    private final CredentialDao credentialDao;
    private final JWTUtils jwtUtils;
    private final SessionDao sessionDao;

    public FBAuthService(FBClient fbClient, EntityManager entityManager,
                         UserDao userDao, CredentialDao credentialDao,
                         SessionDao sessionDao, JWTUtils jwtUtils) {
        this.fbClient = fbClient;
        this.userDao = userDao;
        this.entityManager = entityManager;
        this.credentialDao = credentialDao;
        this.jwtUtils = jwtUtils;
        this.sessionDao = sessionDao;
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

            //
            // Persist session
            Session session = jwtUtils.makeSessionFor(user);
            sessionDao.save(session);

            loginResponse.setSuccess(true);
            loginResponse.setMessage("Login successful");
            loginResponse.setJwt(session.getToken());
            loginResponse.setUser(user);
        } else {
            loginResponse.setMessage("Provided token is invalid");
        }

        return loginResponse;
    }
}
