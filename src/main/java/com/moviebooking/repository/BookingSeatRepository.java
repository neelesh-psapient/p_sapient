package com.moviebooking.repository;

import com.moviebooking.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for BookingSeat entity
 * 
 * CRITICAL: This repository contains queries for checking seat availability
 * and preventing double booking
 */
@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {
    
    /**
     * Check if seats are already booked for a show
     * Used for validation before booking to prevent double booking
     * 
     * Returns seat IDs that are already booked (CONFIRMED or PENDING status)
     * 
     * @param showId Show ID
     * @param seatIds List of seat IDs to check
     * @return List of seat IDs that are already booked
     */
    @Query("""
        SELECT bs.seat.id FROM BookingSeat bs
        WHERE bs.show.id = :showId
        AND bs.seat.id IN :seatIds
        AND bs.status IN ('CONFIRMED', 'PENDING')
        """)
    List<Long> findBookedSeatIds(
        @Param("showId") Long showId,
        @Param("seatIds") List<Long> seatIds
    );
    
    /**
     * Count available seats for a show
     * Used for displaying available seat count in browse API
     * 
     * @param showId Show ID
     * @param screenId Screen ID
     * @return Number of available seats
     */
    @Query("""
        SELECT COUNT(s) FROM Seat s
        WHERE s.screen.id = :screenId
        AND s.id NOT IN (
            SELECT bs.seat.id FROM BookingSeat bs
            WHERE bs.show.id = :showId
            AND bs.status IN ('CONFIRMED', 'PENDING')
        )
        """)
    Integer countAvailableSeats(
        @Param("showId") Long showId,
        @Param("screenId") Long screenId
    );
    
    /**
     * Find booking seats by booking ID
     * @param bookingId Booking ID
     * @return List of booking seats
     */
    List<BookingSeat> findByBookingId(Long bookingId);
}
