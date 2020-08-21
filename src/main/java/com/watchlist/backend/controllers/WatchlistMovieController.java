package com.watchlist.backend.controllers;

import com.watchlist.backend.entities.MoviePost;
import com.watchlist.backend.entities.UpdateWatchlistHasMovie;
import com.watchlist.backend.exceptions.DuplicatedWatchlistMovieException;
import com.watchlist.backend.exceptions.WatchlistHasMovieNotFoundException;
import com.watchlist.backend.exceptions.WatchlistNotFoundException;
import com.watchlist.backend.model.WatchlistHasMovie;
import com.watchlist.backend.services.WatchlistMovieService;
import com.watchlist.backend.services.WatchlistService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("lists/{listId}/movies")
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

    @PostMapping
    public WatchlistHasMovie addMovie(@PathVariable long listId,
                                      @Valid @RequestBody MoviePost moviePost) {
        if (!watchlistService.exists(listId)) {
            throw new WatchlistNotFoundException();
        }

        if (watchlistMovieService.existsByWatchlistAndTmdbId(
                listId,
                moviePost.getTmdbId())) {
            throw new DuplicatedWatchlistMovieException();
        }

        return watchlistMovieService.addMovie(listId, moviePost);
    }

    @PutMapping("{hasMovieId}")
    public WatchlistHasMovie updateMovie(@PathVariable long listId,
                                         @PathVariable long hasMovieId,
                                         @Valid @RequestBody UpdateWatchlistHasMovie updateHasMovie)
            throws Throwable {
        if (!watchlistService.exists(listId)) {
            throw new WatchlistNotFoundException();
        }

        if (!watchlistMovieService.exists(hasMovieId)) {
            throw new WatchlistHasMovieNotFoundException();
        }

        updateHasMovie.setHasMovieId(hasMovieId);
        return watchlistMovieService.updateHasMovie(updateHasMovie);
    }
}
