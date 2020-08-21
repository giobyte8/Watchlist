package com.watchlist.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class WatchlistHasMovieNotFoundException extends RuntimeException {

    public WatchlistHasMovieNotFoundException() {
        super("Watchlist has movie not found");
    }
}
