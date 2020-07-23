package com.watchlist.backend.entities;

import java.util.ArrayList;
import java.util.List;

public class ValidationErrorsResponse {
    private String message;
    private List<ValidationError> errors = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidationError> errors) {
        this.errors = errors;
    }
}
