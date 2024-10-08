package com.example.joke_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JokeFetchException.class)
    public ResponseEntity<String> jokeFetchException(JokeFetchException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
                "Service failed: " + ex.getMessage()
        );
    }

    @ExceptionHandler(InvalidJokeException.class)
    public ResponseEntity<String> invalidJokeException(InvalidJokeException ex) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                "No Content: " + ex.getMessage()
        );
    }
}