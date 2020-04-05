package com.watchlist.backend.security;

/**
 * Used for authentication, this class wraps the representation
 * of principal
 */
public class UserPrincipal {

    private long id;
    private String email;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
