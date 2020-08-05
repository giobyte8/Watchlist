package com.watchlist.backend.controllers;

import com.watchlist.backend.entities.ValidationError;
import com.watchlist.backend.entities.ValidationErrorsResponse;
import com.watchlist.backend.exceptions.NonUniqueWatchlistNameForUserException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
public class ControllersExceptionHandler {
    private static final String INVALID_ENTITY_MSG = "Posted entity is invalid";

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationErrorsResponse handleValidationException(
            MethodArgumentNotValidException ex) {
        ValidationErrorsResponse response = new ValidationErrorsResponse();
        response.setMessage(INVALID_ENTITY_MSG);

        ex.getBindingResult().getAllErrors().forEach(error -> {
            ValidationError validationError = new ValidationError();

            if (error instanceof FieldError) {
                validationError.setField(((FieldError) error).getField());
            } else {
                validationError.setField("multiple");
            }

            validationError.setMessage(error.getDefaultMessage());

            response.getErrors().add(validationError);
        });

        return response;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(NonUniqueWatchlistNameForUserException.class)
    public ValidationErrorsResponse handleValidationException(
            NonUniqueWatchlistNameForUserException ex) {
        ValidationErrorsResponse response = new ValidationErrorsResponse();
        response.setMessage(INVALID_ENTITY_MSG);

        ValidationError error = new ValidationError();
        error.setField("name");
        error.setMessage(ex.getMessage());

        response.getErrors().add(error);
        return response;
    }
}
