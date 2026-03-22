package com.moviebooking.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class SeatAlreadyBookedException extends RuntimeException {
    private final List<String> seats;
    
    public SeatAlreadyBookedException(String message, List<String> seats) {
        super(message);
        this.seats = seats;
    }
}
