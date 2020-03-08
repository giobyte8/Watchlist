package com.watchlist.backend.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "credential")
public class Credential {

    @Id
    private long id;

    @Size(max = 1000)
    @NotNull
    private String token;

    @ManyToOne
    @JoinColumn(name = "auth_provider_id")
    @NotNull
    private AuthProvider authProvider;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AuthProvider getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(AuthProvider authProvider) {
        this.authProvider = authProvider;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
