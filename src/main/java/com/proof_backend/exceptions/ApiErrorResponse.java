package com.proof_backend.exceptions;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    private HttpStatus code;

    private String message;

    private String description;

    public ApiErrorResponse(HttpStatus statusCode) {
        this.code = statusCode;
        this.message = "Unexpected error";
        this.description = "No error description provided";
    }

    public ApiErrorResponse(HttpStatus statusCode, String message, String description) {
        this.code = statusCode;
        this.message = message;
        this.description = description;
    }
}
