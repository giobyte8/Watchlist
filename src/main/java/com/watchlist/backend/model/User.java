package com.watchlist.backend.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @OneToMany(mappedBy = "user")
    private Collection<Credential> credentials;

    @ManyToOne
    @JoinColumn(name = "role_id")
    @NotNull
    private Role role;

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
}
