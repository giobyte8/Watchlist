package com.watchlist.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicatedWatchlistMovieException extends RuntimeException {

    public DuplicatedWatchlistMovieException() {
        super("Given movie is already in this watchlist");
    }
}
