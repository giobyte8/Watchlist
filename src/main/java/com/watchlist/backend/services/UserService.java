package com.watchlist.backend.services;

import com.watchlist.backend.dao.CredentialDao;
import com.watchlist.backend.dao.SessionDao;
import com.watchlist.backend.dao.UserDao;
import com.watchlist.backend.entities.db.*;
import com.watchlist.backend.entities.json.LoginResponse;
import com.watchlist.backend.entities.json.UserCredentials;
import com.watchlist.backend.security.JWTUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.Optional;

@Service
public class UserService {

    private final UserDao userDao;
    private final CredentialDao credentialDao;
    private final SessionDao sessionDao;
    private final EntityManager entityManager;
    private final JWTUtils jwtUtils;

    public UserService(UserDao userDao, CredentialDao credentialDao,
                       SessionDao sessionDao, EntityManager entityManager,
                       JWTUtils jwtUtils) {
        this.userDao = userDao;
        this.credentialDao = credentialDao;
        this.sessionDao = sessionDao;
        this.entityManager = entityManager;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Creates or updates watcher user data and creates a new session. If user
     * was not previously registered then her default watchlist will be
     * created
     *
     * Following entities will be updated if already exists
     * or inserted if not found:
     * - {@link User}
     * - {@link Credential}
     *
     * Following entities will always be created:
     * - {@link Session}
     *
     * Following entities will only be created if user was not
     * registered previously:
     * - {@link Watchlist} for user's default list
     * - {@link UserHasWatchlist}
     *
     * NOTE: Auth provider must be one of listed in {@link AuthProvider}
     *
     * @param credentials User authentication data
     * @param watchlistService Used to create user's default watchlist if necessary
     * @param loginResponse User and jwt will be set into this container
     */
    @Transactional
    public void upsertWatcher(UserCredentials credentials,
                              WatchlistService watchlistService,
                              LoginResponse loginResponse) {
        User user = upsertUser(credentials, watchlistService);
        upsertCredential(
                user,
                credentials.getAuthProviderId(),
                credentials.getToken()
        );

        Session session = jwtUtils.makeSessionFor(user);
        sessionDao.save(session);

        loginResponse.setUser(user);
        loginResponse.setJwt(session.getToken());
    }

    /**
     * User with provided email will be updated or created. If user
     * is not already registered then her default watchlist will be created
     *
     * @param credentials User authentication data
     * @return Upserted user
     */
    private User upsertUser(UserCredentials credentials,
                            WatchlistService watchlistService) {
        User user = userDao.findByEmail(credentials.getEmail());
        boolean createDefaultList = user == null;

        if (user == null) {

            Role watcherRole = entityManager.getReference(
                    Role.class,
                    Role.WATCHER
            );

            user = new User();
            user.setRole(watcherRole);
        }

        user.setName(credentials.getName());
        user.setEmail(credentials.getEmail());
        user.setPicture(credentials.getPicture());
        userDao.save(user);

        if (createDefaultList) {
            watchlistService.createDefaultList(user);
        }

        return user;
    }

    /**
     * Upsert user credentials for specified auth provider
     *
     * @param user Credentials owner
     * @param authProviderId One of listed in {@link AuthProvider}
     * @param token Access token for indicated provider
     */
    private void upsertCredential(User user, long authProviderId, String token) {
        AuthProvider authProvider = entityManager.getReference(
                AuthProvider.class,
                authProviderId
        );

        Credential storedCredential = credentialDao.findByAuthProviderAndUser(
                authProvider,
                user
        );

        if (storedCredential == null) {
            storedCredential = new Credential();
            storedCredential.setUser(user);
            storedCredential.setAuthProvider(authProvider);
        }
        storedCredential.setToken(token);
        credentialDao.save(storedCredential);
    }

    public boolean userExists(long userId) {
        return userDao.existsById(userId);
    }

    public Collection<Watchlist> getLists(long userId) {
        Optional<User> user = userDao.findById(userId);
        return user.map(User::getWatchlists).orElse(null);
    }
}
