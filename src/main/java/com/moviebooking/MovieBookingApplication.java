package com.moviebooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main application class for Movie Booking Platform
 * 
 * This is a production-quality online movie ticket booking platform
 * supporting B2B (Theatre Partners) and B2C (End Users) operations.
 * 
 * Key Features:
 * - Browse shows by city, movie, and date
 * - Book tickets with seat selection
 * - Concurrency handling using optimistic locking
 * - Discount engine using Strategy Pattern
 * - Transaction management for booking flow
 * 
 * @author Senior Backend Engineer
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class MovieBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieBookingApplication.class, args);
    }
}
