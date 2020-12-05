package com.watchlist.backend.services;

import com.watchlist.backend.dao.CredentialDao;
import com.watchlist.backend.dao.SessionDao;
import com.watchlist.backend.dao.UserDao;
import com.watchlist.backend.entities.db.Session;
import com.watchlist.backend.entities.db.User;
import com.watchlist.backend.entities.json.LoginResponse;
import com.watchlist.backend.entities.json.UserCredentials;
import com.watchlist.backend.security.JWTUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private CredentialDao credentialDao;

    @SuppressWarnings("unused")
    @Mock
    private SessionDao sessionDao;

    @SuppressWarnings("unused")
    @Mock
    private EntityManager entityManager;

    @Mock
    private JWTUtils jwtUtils;

    @InjectMocks
    private UserService userService;

    @Mock
    private WatchlistService watchlistService;

    @Test
    public void testInsertUser() {

        // Given
        LoginResponse response = new LoginResponse();
        UserCredentials credentials = new UserCredentials();
        credentials.setName("John Doe");
        credentials.setEmail("john@example.com");

        Session session = new Session();
        session.setToken("1234");

        // When
        Mockito
                .when(userDao.findByEmail(credentials.getEmail()))
                .thenReturn(null);
        Mockito
                .when(jwtUtils.makeSessionFor(any(User.class)))
                .thenReturn(session);
        userService.upsertWatcher(credentials, watchlistService, response);

        // Then
        Mockito
                .verify(userDao, times(1))
                .save(argThat(user -> user
                        .getEmail()
                        .equals(credentials.getEmail())
                ));
        Mockito
                .verify(watchlistService, times(1))
                .createDefaultList(argThat(user -> user
                        .getEmail()
                        .equals(credentials.getEmail())
                ));
        Mockito
                .verify(credentialDao, times(1))
                .save(argThat(credential -> credential
                        .getUser()
                        .getEmail()
                        .equals(credentials.getEmail())
                ));
        assertEquals(response.getUser().getEmail(), credentials.getEmail());
        assertEquals(response.getJwt(), session.getToken());
    }

    @Test
    public void testUpdateUser() {

        // Given
        LoginResponse response = new LoginResponse();

        UserCredentials credentials = new UserCredentials();
        credentials.setName("John Doe");
        credentials.setEmail("john@example.com");

        User user1 = new User();
        user1.setName("Old name");
        user1.setEmail(credentials.getEmail());

        Session session = new Session();
        session.setToken("1234");

        // When
        Mockito
                .when(userDao.findByEmail(credentials.getEmail()))
                .thenReturn(user1);
        Mockito
                .when(jwtUtils.makeSessionFor(any(User.class)))
                .thenReturn(session);
        userService.upsertWatcher(credentials, watchlistService, response);

        // Then
        Mockito
                .verify(userDao, times(1))
                .save(argThat(user -> user.getEmail().equals(user1.getEmail())
                        && user.getName().equals(credentials.getName())
                ));
        Mockito
                .verify(watchlistService, Mockito.never())
                .createDefaultList(any(User.class));
        Mockito
                .verify(credentialDao, times(1))
                .save(argThat(credential -> credential
                        .getUser()
                        .getEmail()
                        .equals(credentials.getEmail())
                ));
        assertEquals(response.getUser().getName(), credentials.getName());
        assertEquals(response.getUser().getEmail(), credentials.getEmail());
        assertEquals(response.getJwt(), session.getToken());
    }
}
