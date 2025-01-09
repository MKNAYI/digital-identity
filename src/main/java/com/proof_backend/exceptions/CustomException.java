package com.proof_backend.exceptions;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {

    public String message;

    public String description;

    public HttpStatus status;

    public CustomException(String message, HttpStatus status) {
        super(message);
        this.message = message;
        this.status = status;
    }

    public CustomException(String message, String description, HttpStatus status ) {
        super(message);
        this.message = message;
        this.status = status;
        this.description = description;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription(){return description;}
}
