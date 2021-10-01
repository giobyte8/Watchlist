package com.watchlist.backend.controllers;

import com.watchlist.backend.entities.UpdateWatchlistHasMovie;
import com.watchlist.backend.entities.db.WatchlistHasMovie;
import com.watchlist.backend.entities.json.LocalizedListItem;
import com.watchlist.backend.entities.json.WatchlistItemPost;
import com.watchlist.backend.exceptions.DuplicatedWatchlistMovieException;
import com.watchlist.backend.exceptions.WatchlistHasMovieNotFoundException;
import com.watchlist.backend.exceptions.WatchlistNotFoundException;
import com.watchlist.backend.security.UserPrincipal;
import com.watchlist.backend.services.ListMoviesService;
import com.watchlist.backend.services.WatchlistService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("lists/{listId}/movies")
public class ListMoviesController  {

    private final WatchlistService watchlistService;
    private final ListMoviesService listMoviesService;

    public ListMoviesController(WatchlistService watchlistService,
                                ListMoviesService listMoviesService) {
        this.watchlistService = watchlistService;
        this.listMoviesService = listMoviesService;
    }

    @PostMapping
    public LocalizedListItem addMovie(@AuthenticationPrincipal UserPrincipal authPrincipal,
                                      @PathVariable long listId,
                                      @Valid @RequestBody WatchlistItemPost moviePost) {
        if (!watchlistService.exists(listId)) {
            throw new WatchlistNotFoundException();
        }

        if (listMoviesService.existsByWatchlistAndTmdbId(
                listId,
                moviePost.getTmdbId())) {
            throw new DuplicatedWatchlistMovieException();
        }

        return listMoviesService.addMovie(
                listId,
                authPrincipal.getId(),
                moviePost
        );
    }

    @PutMapping("{hasMovieId}")
    public WatchlistHasMovie updateMovie(@PathVariable long listId,
                                         @PathVariable long hasMovieId,
                                         @Valid @RequestBody UpdateWatchlistHasMovie updateHasMovie)
            throws Throwable {
        if (!watchlistService.exists(listId)) {
            throw new WatchlistNotFoundException();
        }

        if (!listMoviesService.exists(hasMovieId)) {
            throw new WatchlistHasMovieNotFoundException();
        }

        updateHasMovie.setHasMovieId(hasMovieId);
        return listMoviesService.updateHasMovie(updateHasMovie);
    }

    @DeleteMapping("{hasMovieId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWatchlistHasMovie(@PathVariable long listId,
                                        @PathVariable long hasMovieId) {
        if (!watchlistService.exists(listId)) {
            throw new WatchlistNotFoundException();
        }

        if (!listMoviesService.exists(hasMovieId)) {
            throw new WatchlistHasMovieNotFoundException();
        }

        listMoviesService.deleteHasMovie(hasMovieId);
    }
}
