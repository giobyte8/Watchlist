package com.watchlist.backend.entities.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.watchlist.backend.entities.db.User;

import java.util.Date;

public class LocalizedListHasMovie {

    private long id;
    private String lang;

    @JsonProperty("added_at")
    private Date addedAt;

    @JsonProperty("seen_at")
    private Date seenAt;

    @JsonProperty("added_by")
    private User addedBy;

    private JLocalizedMovie movie;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Date getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Date addedAt) {
        this.addedAt = addedAt;
    }

    public Date getSeenAt() {
        return seenAt;
    }

    public void setSeenAt(Date seenAt) {
        this.seenAt = seenAt;
    }

    public User getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(User addedBy) {
        this.addedBy = addedBy;
    }

    public JLocalizedMovie getMovie() {
        return movie;
    }

    public void setMovie(JLocalizedMovie movie) {
        this.movie = movie;
    }
}
