package com.watchlist.backend.entities.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Config {

    @JsonProperty("tmdb_api_key")
    private String tmdbApiKey;

    @JsonProperty("yt_api_key")
    private String ytApiKey;

    public String getTmdbApiKey() {
        return tmdbApiKey;
    }

    public void setTmdbApiKey(String tmdbApiKey) {
        this.tmdbApiKey = tmdbApiKey;
    }

    public String getYtApiKey() {
        return ytApiKey;
    }

    public void setYtApiKey(String ytApiKey) {
        this.ytApiKey = ytApiKey;
    }
}
