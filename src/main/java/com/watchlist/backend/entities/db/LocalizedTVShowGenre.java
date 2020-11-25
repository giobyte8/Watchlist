package com.watchlist.backend.entities.db;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "localized_tv_show_genre")
public class LocalizedTVShowGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Size(max = 255)
    private String name;

    @ManyToOne
    @JoinColumn(name = "tv_show_genre_id")
    @NotNull
    private TVShowGenre tvShowGenre;

    @ManyToOne
    @JoinColumn(name = "language_id")
    @NotNull
    private Language language;

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

    public TVShowGenre getTvShowGenre() {
        return tvShowGenre;
    }

    public void setTvShowGenre(TVShowGenre tvShowGenre) {
        this.tvShowGenre = tvShowGenre;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
