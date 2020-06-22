package com.watchlist.backend.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "movie_has_genre")
public class MovieHasGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    @NotNull
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "genre_id")
    @NotNull
    private Genre genre;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }
}
