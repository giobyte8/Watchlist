package com.watchlist.backend.exceptions;

public class NonUniqueWatchlistNameForUserException extends RuntimeException {

    public NonUniqueWatchlistNameForUserException() {
        super("A watchlist with same name already exists for indicated user");
    }
}
