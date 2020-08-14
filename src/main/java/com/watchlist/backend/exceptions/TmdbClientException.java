package com.watchlist.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class TmdbClientException extends RuntimeException {

    public TmdbClientException() {
        super("Something went wrong while connecting with TMDB");
    }
}
