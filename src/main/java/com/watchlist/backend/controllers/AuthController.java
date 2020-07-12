package com.watchlist.backend.controllers;

import com.watchlist.backend.entities.LoginResponse;
import com.watchlist.backend.entities.UserCredentials;
import com.watchlist.backend.services.AuthService;
import com.watchlist.backend.services.UserService;
import com.watchlist.backend.services.WatchlistService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final WatchlistService watchlistService;

    public AuthController(AuthService authService, UserService userService,
                          WatchlistService watchlistService) {
        this.authService = authService;
        this.userService = userService;
        this.watchlistService = watchlistService;
    }

    @PostMapping("login")
    public LoginResponse login(@RequestBody UserCredentials credentials) {
        LoginResponse response = new LoginResponse();

        if (authService.authenticate(credentials)) {
            userService.upsertWatcher(credentials, watchlistService, response);

            response.setSuccess(true);
            response.setMessage("Login successful");
        } else {
            response.setMessage("Provided token or email is invalid");
        }

        return response;
    }
}
