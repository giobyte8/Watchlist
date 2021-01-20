package com.watchlist.backend.entities.db;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tv_show_genre")
public class TVShowGenre {

    @Id
    private long id;

    @OneToMany(mappedBy = "tvShowGenre", fetch = FetchType.EAGER)
    private List<LocalizedTVShowGenre> localizedGenres = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<LocalizedTVShowGenre> getLocalizedGenres() {
        return localizedGenres;
    }

    public void setLocalizedGenres(List<LocalizedTVShowGenre> localizedGenres) {
        this.localizedGenres = localizedGenres;
    }
}
