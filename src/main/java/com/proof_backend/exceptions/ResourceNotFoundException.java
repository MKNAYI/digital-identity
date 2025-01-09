package com.proof_backend.exceptions;

import lombok.Getter;

import static java.lang.String.format;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    public String message;
    public String description;

    public ResourceNotFoundException(final Class resourceClazz, final Long resource) {
        super(format("Resource %s not found for %s", resourceClazz.getSimpleName(), resource));
    }

    public ResourceNotFoundException(final Long resource) {
        super(format("Resource not found with given id: %s", resource));
    }
    public ResourceNotFoundException(final String message) {
        super(message);
        this.message = message;
    }
    public ResourceNotFoundException(final String message, String description) {
        super(message);
        this.message = message;
        this.description = description;
    }

}
