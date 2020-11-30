package com.watchlist.backend.entities.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WatchlistItemPut {

    @JsonProperty("seen_at")
    private String seenAt;

    public String getSeenAt() {
        return seenAt;
    }

    public void setSeenAt(String seenAt) {
        this.seenAt = seenAt;
    }
}
