package com.watchlist.backend.entities.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "auth_provider")
public class AuthProvider {
    public static final long FB_AUTH_PROVIDER = 1;
    public static final long GO_AUTH_PROVIDER = 2;

    @Id
    private long id;

    @Column(nullable = false)
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
