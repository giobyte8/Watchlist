package com.watchlist.backend.controllers;

import com.watchlist.backend.entities.LoginResponse;
import com.watchlist.backend.entities.UserCredentials;
import com.watchlist.backend.model.AuthProvider;
import com.watchlist.backend.services.FBAuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthController {

    private final FBAuthService fbAuthService;

    public AuthController(FBAuthService fbAuthService) {
        this.fbAuthService = fbAuthService;
    }

    @PostMapping("login")
    public LoginResponse login(@RequestBody UserCredentials credentials) {
        LoginResponse response;

        if (credentials.getAuthProviderId() == AuthProvider.FB_AUTH_PROVIDER) {
            response = fbAuthService.login(credentials);
        } else {
            response = new LoginResponse();
            response.setMessage("Unsupported auth provider");
        }

        return response;
    }
}
