package com.watchlist.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ProvidedUserIdNotFoundException extends RuntimeException {

    public ProvidedUserIdNotFoundException() {
        super("Invalid user id");
    }
}
