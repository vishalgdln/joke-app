package com.example.joke_app.exception;

public class InvalidJokeException extends RuntimeException {
    public InvalidJokeException(String message) {
        super(message);
    }
}
