package com.watchlist.backend.entities.db;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "watchlist_has_movie")
public class WatchlistHasMovie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "added_at")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date addedAt = new Date();

    @Column(name = "seen_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date seenAt;

    @ManyToOne
    @JoinColumn(name = "watchlist_id")
    @NotNull
    private Watchlist watchlist;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "movie_id")
    @NotNull
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "added_by")
    @NotNull
    private User addedBy;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Date addedAt) {
        this.addedAt = addedAt;
    }

    public Date getSeenAt() {
        return seenAt;
    }

    public void setSeenAt(Date seenAt) {
        this.seenAt = seenAt;
    }

    public Watchlist getWatchlist() {
        return watchlist;
    }

    public void setWatchlist(Watchlist watchlist) {
        this.watchlist = watchlist;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public User getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(User addedBy) {
        this.addedBy = addedBy;
    }
}
