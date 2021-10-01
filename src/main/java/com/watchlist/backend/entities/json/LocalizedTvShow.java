package com.watchlist.backend.entities.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.watchlist.backend.entities.db.TVShowCast;
import com.watchlist.backend.entities.db.TVShowCrew;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties({
        "lang",
        "mediaType"
})
public class LocalizedTvShow extends LocalizedListItem {

    private double rating;

    private List<TVShowCast> cast = new ArrayList<>();
    private List<TVShowCrew> crew = new ArrayList<>();

    @JsonProperty("first_air_date")
    @Override
    public Date getReleaseDate() {
        return super.getReleaseDate();
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<TVShowCast> getCast() {
        return cast;
    }

    public void setCast(List<TVShowCast> cast) {
        this.cast = cast;
    }

    public List<TVShowCrew> getCrew() {
        return crew;
    }

    public void setCrew(List<TVShowCrew> crew) {
        this.crew = crew;
    }
}
