package com.watchlist.backend.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "crew")
public class Crew {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(max = 500)
    private String name;

    @Column(name = "character_name")
    @NotNull
    @Size(max = 500)
    private String characterName;

    @Column(name = "picture_url")
    @Size(max = 5000)
    private String pictureUrl;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    @NotNull
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "crew_category_id")
    @NotNull
    private CrewCategory crewCategory;

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

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public CrewCategory getCrewCategory() {
        return crewCategory;
    }

    public void setCrewCategory(CrewCategory crewCategory) {
        this.crewCategory = crewCategory;
    }
}
