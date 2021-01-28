package com.watchlist.backend.entities.db;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tv_show")
public class TVShow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "tmdb_id")
    private int tmdbId;

    @Column(name = "original_title")
    @Size(max = 255)
    @NotBlank
    private String originalTitle;

    @Column(name = "first_air_date")
    @Temporal(TemporalType.DATE)
    private Date firstAirDate;

    private double rating;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date createdAt = new Date();

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date updatedAt = new Date();

    @OneToMany(mappedBy = "tvShow", fetch = FetchType.EAGER)
    private List<LocalizedTVShow> localizedTVShows = new ArrayList<>();

    @OneToMany(mappedBy = "tvShow")
    private List<TVShowCrew> crew = new ArrayList<>();

    @OneToMany(mappedBy = "tvShow")
    private List<TVShowCast> cast = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "tv_show_has_genre",
            joinColumns = { @JoinColumn(name = "tv_show_id") },
            inverseJoinColumns = { @JoinColumn(name = "tv_show_genre_id") }
    )
    private List<TVShowGenre> genres = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(int tmdbId) {
        this.tmdbId = tmdbId;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public Date getFirstAirDate() {
        return firstAirDate;
    }

    public void setFirstAirDate(Date firstAirDate) {
        this.firstAirDate = firstAirDate;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
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

    public List<TVShowGenre> getGenres() {
        return genres;
    }

    public void setGenres(List<TVShowGenre> genres) {
        this.genres = genres;
    }

    public List<LocalizedTVShow> getLocalizedTVShows() {
        return localizedTVShows;
    }

    public void setLocalizedTVShows(List<LocalizedTVShow> localizedTVShows) {
        this.localizedTVShows = localizedTVShows;
    }

    public List<TVShowCrew> getCrew() {
        return crew;
    }

    public void setCrew(List<TVShowCrew> crew) {
        this.crew = crew;
    }

    public List<TVShowCast> getCast() {
        return cast;
    }

    public void setCast(List<TVShowCast> cast) {
        this.cast = cast;
    }
}
