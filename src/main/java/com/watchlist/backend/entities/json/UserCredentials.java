package com.watchlist.backend.entities.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserCredentials {
    private String name;
    private String email;
    private String picture;
    private String token;

    @JsonProperty("auth_provider_id")
    private long authProviderId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getAuthProviderId() {
        return authProviderId;
    }

    public void setAuthProviderId(long authProviderId) {
        this.authProviderId = authProviderId;
    }
}
