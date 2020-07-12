package com.watchlist.backend.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "watchlist_permission")
public class WatchlistPermission {
    public static long OWNER_ID = 1;
    public static long COLLABORATOR_ID = 2;
    public static long FOLLOWER_ID = 3;

    @Id
    private long id;

    @NotNull
    @Size(max = 255)
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
