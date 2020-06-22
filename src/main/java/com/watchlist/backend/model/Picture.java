package com.watchlist.backend.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "picture")
public class Picture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(max = 5000)
    private String url;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    @NotNull
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "picture_category_id")
    @NotNull
    private PictureCategory category;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public PictureCategory getCategory() {
        return category;
    }

    public void setCategory(PictureCategory category) {
        this.category = category;
    }
}
