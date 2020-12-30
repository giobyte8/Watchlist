package com.watchlist.backend.entities.db;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movie_genre")
public class MovieGenre {

    @Id
    private long id;

    @OneToMany(mappedBy = "movieGenre")
    private List<LocalizedMovieGenre> localizedGenres = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<LocalizedMovieGenre> getLocalizedGenres() {
        return localizedGenres;
    }

    public void setLocalizedGenres(List<LocalizedMovieGenre> localizedGenres) {
        this.localizedGenres = localizedGenres;
    }
}
