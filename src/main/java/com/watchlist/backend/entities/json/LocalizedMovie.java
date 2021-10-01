package com.watchlist.backend.entities.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.watchlist.backend.entities.db.MovieCast;
import com.watchlist.backend.entities.db.MovieCrew;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({
        "lang",
        "mediaType"
})
public class LocalizedMovie extends LocalizedListItem {

    @JsonProperty("tmdb_id")
    private int tmdbId;

    private int runtime;
    private double rating;

    private List<MovieCast> cast = new ArrayList<>();
    private List<MovieCrew> crew = new ArrayList<>();

    @Override
    public int getTmdbId() {
        return tmdbId;
    }

    @Override
    public void setTmdbId(int tmdbId) {
        this.tmdbId = tmdbId;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<MovieCast> getCast() {
        return cast;
    }

    public void setCast(List<MovieCast> cast) {
        this.cast = cast;
    }

    public List<MovieCrew> getCrew() {
        return crew;
    }

    public void setCrew(List<MovieCrew> crew) {
        this.crew = crew;
    }
}
