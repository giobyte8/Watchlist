package com.watchlist.backend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Deprecated
public class UpdateWatchlistHasMovie {

    private long hasMovieId;

    @JsonProperty("seen_at")
    @NotNull
    private Date seenAt;

    public long getHasMovieId() {
        return hasMovieId;
    }

    public void setHasMovieId(long hasMovieId) {
        this.hasMovieId = hasMovieId;
    }

    public Date getSeenAt() {
        return seenAt;
    }

    public void setSeenAt(Date seenAt) {
        this.seenAt = seenAt;
    }
}
