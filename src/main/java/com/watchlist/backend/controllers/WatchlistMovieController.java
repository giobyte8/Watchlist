package com.watchlist.backend.controllers;

import com.watchlist.backend.exceptions.WatchlistNotFoundException;
import com.watchlist.backend.model.WatchlistHasMovie;
import com.watchlist.backend.services.WatchlistMovieService;
import com.watchlist.backend.services.WatchlistService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("list/{listId}/movies")
public class WatchlistMovieController {

    private final WatchlistService watchlistService;
    private final WatchlistMovieService watchlistMovieService;

    public WatchlistMovieController(WatchlistService watchlistService,
                                    WatchlistMovieService watchlistMovieService) {
        this.watchlistService = watchlistService;
        this.watchlistMovieService = watchlistMovieService;
    }

    @GetMapping
    public Collection<WatchlistHasMovie> getMovies(@PathVariable long listId) {
        if (!watchlistService.exists(listId)) {
            throw new WatchlistNotFoundException();
        }

        return watchlistMovieService.getMovies(listId);
    }
}
