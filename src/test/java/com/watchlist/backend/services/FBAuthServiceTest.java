package com.watchlist.backend.services;

import com.watchlist.backend.clients.FBClient;
import com.watchlist.backend.entities.UserCredentials;
import com.watchlist.backend.exceptions.WrongAuthProviderException;
import com.watchlist.backend.model.AuthProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class FBAuthServiceTest {

    @Mock
    private FBClient mFBClient;

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
    public void testRightToken() {

        // Given
        testCredentials.setAuthProviderId(AuthProvider.FB_AUTH_PROVIDER);

        // When
        Mockito
                .when(mFBClient.verifyToken(
                        testCredentials.getEmail(),
                        testCredentials.getToken()
                ))
                .thenReturn(true);
        boolean loginResult = fbAuthService.authenticate(testCredentials);

        // Then
        Mockito
                .verify(mFBClient, times(1))
                .verifyToken(
                        testCredentials.getEmail(),
                        testCredentials.getToken()
                );
        assertTrue(
                "Login result should be successful",
                loginResult
        );
    }

    @Test
    public void testWrongToken() {

        // Given
        testCredentials.setAuthProviderId(AuthProvider.FB_AUTH_PROVIDER);

        // When
        Mockito
                .when(mFBClient.verifyToken(
                    testCredentials.getEmail(),
                    testCredentials.getToken()
                ))
                .thenReturn(false);
        boolean loginResult = fbAuthService.authenticate(testCredentials);

        // Then
        Mockito
                .verify(mFBClient, times(1))
                .verifyToken(
                        testCredentials.getEmail(),
                        testCredentials.getToken()
                );
        assertFalse(
                "Login result should be unsuccessful",
                loginResult
        );
    }

    @Test(expected = WrongAuthProviderException.class)
    public void testWrongAuthProviderId() {

        // Given
        testCredentials.setAuthProviderId(AuthProvider.GO_AUTH_PROVIDER);
        fbAuthService.authenticate(testCredentials);

        // Then exception should be thrown
    }
}
