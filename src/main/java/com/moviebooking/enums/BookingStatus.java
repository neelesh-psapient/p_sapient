package com.moviebooking.enums;

/**
 * Enum representing the status of a booking
 * 
 * PENDING: Booking created, payment in progress
 * CONFIRMED: Payment successful, booking confirmed
 * CANCELLED: Booking cancelled by user
 * FAILED: Payment failed or booking failed
 */
public enum BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    FAILED
}
