package com.watchlist.backend.services;

import com.watchlist.backend.clients.FBClient;
import com.watchlist.backend.dao.CredentialDao;
import com.watchlist.backend.dao.SessionDao;
import com.watchlist.backend.dao.UserDao;
import com.watchlist.backend.entities.LoginResponse;
import com.watchlist.backend.entities.UserCredentials;
import com.watchlist.backend.model.AuthProvider;
import com.watchlist.backend.model.Role;
import com.watchlist.backend.model.Session;
import com.watchlist.backend.model.User;
import com.watchlist.backend.security.JWTUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FBAuthServiceTest {

    @Mock
    private FBClient mockFBClient;

    @Mock
    private UserDao mockUserDao;

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private CredentialDao mockCredentialDao;

    @Mock
    private JWTUtils mockJWTUtils;

    @Mock
    private SessionDao mockSessionDao;

    @InjectMocks
    private FBAuthService fbAuthService;

    private UserCredentials testCredentials;

    @Before
    public void setup() {
        testCredentials = new UserCredentials();
        testCredentials.setName("John Doe");
        testCredentials.setEmail("test@example.com");
        testCredentials.setToken("12345");
        testCredentials.setPicture("https://example.com/1.png");
    }

    @Test
    public void loginWithRightToken() {
        Role watcher = new Role();
        watcher.setId(Role.WATCHER);

        AuthProvider fbAuthProvider = new AuthProvider();
        fbAuthProvider.setId(AuthProvider.FB_AUTH_PROVIDER);

        Session fakeSession = new Session();
        fakeSession.setExpiration(new Date());
        fakeSession.setToken("gen_token_123");
        fakeSession.setUser(new User());

        when(mockFBClient.verifyToken(testCredentials.getEmail(), testCredentials.getToken()))
                .thenReturn(true);
        when(mockEntityManager.getReference(Role.class, Role.WATCHER))
                .thenReturn(watcher);
        when(mockEntityManager.getReference(AuthProvider.class, AuthProvider.FB_AUTH_PROVIDER))
                .thenReturn(fbAuthProvider);
        when(mockJWTUtils.makeSessionFor(any(User.class)))
                .thenReturn(fakeSession);

        LoginResponse loginResponse = fbAuthService.login(testCredentials);

        verify(mockUserDao, times(1)).save(argThat(user ->
                user.getName().equals(testCredentials.getName()) &&
                        user.getEmail().equals(testCredentials.getEmail()) &&
                        user.getRole().equals(watcher)
                ));
        verify(mockCredentialDao, times(1)).save(argThat(credential ->
                credential.getAuthProvider().equals(fbAuthProvider) &&
                        credential.getUser().getEmail().equals(testCredentials.getEmail()) &&
                        credential.getToken().equals(testCredentials.getToken())
                ));
        verify(mockSessionDao, times(1)).save(
                argThat(session -> session.equals(fakeSession)));

        assertTrue("Login should be successful", loginResponse.isSuccess());
    }

    @Test
    public void loginWithWrongToken() {
        when(mockFBClient.verifyToken(testCredentials.getEmail(), testCredentials.getToken()))
                .thenReturn(false);

        LoginResponse loginResponse = fbAuthService.login(testCredentials);
        assertFalse("Login should be unsuccessful", loginResponse.isSuccess());
    }
}
