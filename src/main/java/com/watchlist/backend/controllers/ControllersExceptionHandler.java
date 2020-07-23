package com.watchlist.backend.controllers;

import com.watchlist.backend.entities.ValidationError;
import com.watchlist.backend.entities.ValidationErrorsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
public class ControllersExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationErrorsResponse handleValidationException(
            MethodArgumentNotValidException ex) {
        ValidationErrorsResponse response = new ValidationErrorsResponse();
        response.setMessage("Posted entity is invalid");

        ex.getBindingResult().getAllErrors().forEach(error -> {
            ValidationError validationError = new ValidationError();
            validationError.setField(((FieldError) error).getField());
            validationError.setMessage(error.getDefaultMessage());

            response.getErrors().add(validationError);
        });

        return response;
    }
}
