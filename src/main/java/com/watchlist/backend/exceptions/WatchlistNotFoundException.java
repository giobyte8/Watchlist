package com.watchlist.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        value = HttpStatus.NOT_FOUND,
        reason = "Watchlist not found"
)
public class WatchlistNotFoundException extends RuntimeException {
}
