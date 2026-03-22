package com.moviebooking.entity;

import com.moviebooking.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entity representing a junction table between Booking and Seat
 * 
 * CRITICAL: This entity has a unique constraint on (show_id, seat_id)
 * to prevent double booking at the database level
 * 
 * This provides an additional layer of protection against concurrent bookings
 * beyond the optimistic locking on the Booking entity
 */
@Entity
@Table(name = "booking_seats",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_show_seat",
           columnNames = {"show_id", "seat_id"}
       ),
       indexes = @Index(name = "idx_booking_seat_show_seat", columnList = "show_id,seat_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingSeat extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status;
}
