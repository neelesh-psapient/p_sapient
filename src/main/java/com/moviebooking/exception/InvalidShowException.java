package com.moviebooking.exception;

public class InvalidShowException extends RuntimeException {
    public InvalidShowException(String message) {
        super(message);
    }
}
