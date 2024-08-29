package com.example.joke_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Service failed: " + e.getMessage());
    }

    @ExceptionHandler(JokeFetchException.class)
    public ResponseEntity<String> serverSideException(JokeFetchException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }
}