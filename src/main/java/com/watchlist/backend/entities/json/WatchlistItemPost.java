package com.watchlist.backend.entities.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;

public class WatchlistItemPost {

    @JsonProperty("tmdb_id")
    @Min(1)
    private int tmdbId;

    private String lang;

    public int getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(int tmdbId) {
        this.tmdbId = tmdbId;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
