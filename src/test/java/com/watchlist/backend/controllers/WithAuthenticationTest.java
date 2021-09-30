package com.watchlist.backend.controllers;

import com.watchlist.backend.security.JWTUtils;
import com.watchlist.backend.security.UserPrincipal;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class WithAuthenticationTest {

    @MockBean
    private JWTUtils mockJwtUtils;

    private final String testToken = "testToken";

    public MockHttpServletRequestBuilder authenticatedPostBuilder(
            String urlTemplate,
            Object... uriParams) {
        return authenticatedPostBuilder(
                1,
                urlTemplate,
                uriParams
        );
    }

    public MockHttpServletRequestBuilder authenticatedPostBuilder(
            long userId,
            String urlTemplate,
            Object... uriParams) {
        setupAuthMocks(userId);
        return post(urlTemplate, uriParams).header(
                "Authorization",
                testToken
        );
    }

    private void setupAuthMocks(long userId) {
        Mockito
                .when(mockJwtUtils.isValid(testToken))
                .thenReturn(true);

        UserPrincipal principal = new UserPrincipal();
        principal.setId(userId);
        principal.setEmail("test@email.com");

        Mockito
                .when(mockJwtUtils.getUserPrincipal(anyString()))
                .thenReturn(principal);
    }

}
