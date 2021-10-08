package com.watchlist.backend.entities.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class WatchlistItemPatch {

    @JsonProperty("seen_at")
    private Date seenAt;

    public Date getSeenAt() {
        return seenAt;
    }

    public void setSeenAt(Date seenAt) {
        this.seenAt = seenAt;
    }
}
