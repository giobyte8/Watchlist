package com.watchlist.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        value = HttpStatus.NOT_FOUND,
        reason = "Element was not found in requested list"
)
public class ListHasItemNotFoundException extends RuntimeException {

}
