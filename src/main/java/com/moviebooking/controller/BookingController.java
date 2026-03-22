package com.moviebooking.controller;

import com.moviebooking.dto.request.BookingRequest;
import com.moviebooking.dto.response.BookingResponse;
import com.moviebooking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for booking-related endpoints
 */
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    
    private final BookingService bookingService;
    
    /**
     * Create a new booking
     * 
     * @param request Booking request with user, show, seats, and payment method
     * @return Booking confirmation with booking ID and amount
     */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        log.info("POST /api/v1/bookings - request: {}", request);
        
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Get user bookings
     * 
     * @param userId User ID
     * @return List of user bookings
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponse>> getUserBookings(@PathVariable Long userId) {
        log.info("GET /api/v1/bookings/user/{}", userId);
        
        List<BookingResponse> bookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }
}
