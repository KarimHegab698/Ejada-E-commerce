package com.example.shop_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({CartNotFoundException.class, OrderNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex){
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(CheckoutFailedException.class)
    public ResponseEntity<ErrorResponse> handleCheckoutFailed(CheckoutFailedException ex){
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex){
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        ErrorResponse body = new ErrorResponse(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message);
        return ResponseEntity.status(status).body(body);
    }
}
