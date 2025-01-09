package com.proof_backend.exceptions;

import com.stripe.exception.StripeException;
import com.stripe.model.StripeError;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@AllArgsConstructor
@Slf4j
public class DigitalIdentifierExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Handling resource not found exception : {}", ex.getMessage(), ex);

        ApiErrorResponse apiError = new ApiErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ex.getDescription());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(InternalServerException.class)
    public final ResponseEntity<Object> handleInternalServerException(InternalServerException ex) {
        log.error("Handling internal server exception : {}", ex.getMessage(), ex);

        ApiErrorResponse apiError = new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Oops! Something went wrong, Please try again.", ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public final ResponseEntity<Object> handleInvalidRequestException(InvalidRequestException ex) {
        log.error("Handling request not valid exception : {}", ex.getMessage(), ex);

        ApiErrorResponse apiError = new ApiErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiErrorResponse apiError) {
        return new ResponseEntity<>(apiError, apiError.getCode());
    }

    @ExceptionHandler(CustomException.class)
    public final ResponseEntity<Object> handleCustomException(CustomException ex) {
        log.error("Handling custom exception : {}", ex.getMessage(), ex);

        ApiErrorResponse apiError = new ApiErrorResponse(ex.status, ex.getMessage(), ex.getDescription());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        log.error("Handling bad request exception : {}", ex.getMessage(), ex);
        ApiErrorResponse apiError = new ApiErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(StripeException.class)
    public final ResponseEntity<Object> handleStripeException(StripeException ex) {
        log.error("Handling stripe exception : {}", ex.getStripeError().getMessage(), ex);

        ApiErrorResponse apiError = new ApiErrorResponse(HttpStatus.valueOf(ex.getStatusCode()), ex.getStripeError().getMessage(), ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleException(Exception ex) {
        log.error("Handling exception : {}", ex.getMessage(), ex);
        ApiErrorResponse apiError = new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Oops! Something went wrong, Please try again.", ex.getMessage());
        return buildResponseEntity(apiError);
    }
}