package com.watchlist.backend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;

public class MoviePost {

    @JsonProperty("tmdb_id")
    @Min(1)
    private int tmdbId;

    @JsonProperty("added_by")
    @Min(1)
    private long addedBy;

    public int getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(int tmdbId) {
        this.tmdbId = tmdbId;
    }

    public long getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(long addedBy) {
        this.addedBy = addedBy;
    }
}
