package com.example.joke_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JokeFetchException.class)
    public ResponseEntity<String> serverSideException(JokeFetchException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
                "Service failed: " + ex.getMessage()
        );
    }

    @ExceptionHandler(ValidCountException.class)
    public ResponseEntity<String> validCountException(ValidCountException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                "Bad Request: " + ex.getMessage()
        );
    }
}