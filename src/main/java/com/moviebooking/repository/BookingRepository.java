package com.moviebooking.repository;

import com.moviebooking.entity.Booking;
import com.moviebooking.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Booking entity
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    /**
     * Find bookings by user ID ordered by creation date
     * @param userId User ID
     * @return List of user bookings
     */
    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Find bookings by show ID
     * @param showId Show ID
     * @return List of bookings for the show
     */
    List<Booking> findByShowId(Long showId);
    
    /**
     * Find bookings by user ID and status
     * @param userId User ID
     * @param status Booking status
     * @return List of bookings matching criteria
     */
    List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);
    
    /**
     * Find booking with booking seats
     * @param bookingId Booking ID
     * @return Booking with booking seats eagerly loaded
     */
    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.bookingSeats WHERE b.id = :bookingId")
    Booking findByIdWithBookingSeats(@Param("bookingId") Long bookingId);
}
