package com.moviebooking.exception;

public class InvalidSeatException extends RuntimeException {
    public InvalidSeatException(String message) {
        super(message);
    }
}
