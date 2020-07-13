package com.watchlist.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class WrongAuthProviderException extends RuntimeException {

    public WrongAuthProviderException(String message) {
        super(message);
    }
}
