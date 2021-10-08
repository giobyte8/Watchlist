package com.watchlist.backend.controllers;

import com.watchlist.backend.security.JWTUtils;
import com.watchlist.backend.security.UserPrincipal;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class WithAuthenticationTest {

    @MockBean
    private JWTUtils mockJwtUtils;

    private final String testToken = "testToken";
    private final long defaultAuthUserId = 1;

    public MockHttpServletRequestBuilder authPostBuilder(
            String urlTemplate,
            Object... uriParams) {
        return authPostBuilder(
                defaultAuthUserId,
                urlTemplate,
                uriParams
        );
    }

    public MockHttpServletRequestBuilder authPostBuilder(
            MediaType mediaType,
            String payload,
            long authUserId,
            String urlTemplate,
            Object... uriParams) {

        return authPostBuilder(authUserId, urlTemplate, uriParams)
                .contentType(mediaType)
                .content(payload);
    }

    public MockHttpServletRequestBuilder authPostBuilder(
            MediaType mediaType,
            String payload,
            String urlTemplate,
            Object... uriParams) {

        return authPostBuilder(defaultAuthUserId, urlTemplate, uriParams)
                .contentType(mediaType)
                .content(payload);
    }

    public MockHttpServletRequestBuilder authPostBuilder(
            long authUserId,
            String urlTemplate,
            Object... uriParams) {

        setupAuthMocks(authUserId);

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
