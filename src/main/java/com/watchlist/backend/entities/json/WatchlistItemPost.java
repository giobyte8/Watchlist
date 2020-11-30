package com.watchlist.backend.entities.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WatchlistItemPost {

    @JsonProperty("tmdb_id")
    private int tmdbId;

    private String language;

    public int getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(int tmdbId) {
        this.tmdbId = tmdbId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
