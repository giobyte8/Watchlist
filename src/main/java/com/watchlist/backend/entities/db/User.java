package com.watchlist.backend.entities.db;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Size(max = 500)
    @NotNull
    private String name;

    @Size(max = 1000)
    @NotNull
    private String email;

    @Size(max = 5000)
    @NotNull
    private String picture;

    @Column(name = "last_login")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date lastLogin;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date createdAt = new Date();

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date updatedAt = new Date();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Collection<Credential> credentials;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Collection<Session> sessions;

    @ManyToOne
    @JoinColumn(name = "role_id")
    @NotNull
    @JsonIgnore
    private Role role;

    @ManyToMany
    @JoinTable(
            name = "user_has_watchlist",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "watchlist_id") }
    )
    @JsonIgnore
    private Collection<Watchlist> watchlists;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Collection<Credential> getCredentials() {
        return credentials;
    }

    public void setCredentials(Collection<Credential> credentials) {
        this.credentials = credentials;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Collection<Session> getSessions() {
        return sessions;
    }

    public void setSessions(Collection<Session> sessions) {
        this.sessions = sessions;
    }

    public Collection<Watchlist> getWatchlists() {
        return watchlists;
    }

    public void setWatchlists(Collection<Watchlist> watchlists) {
        this.watchlists = watchlists;
    }
}
