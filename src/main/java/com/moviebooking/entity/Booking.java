package com.moviebooking.entity;

import com.moviebooking.enums.BookingStatus;
import com.moviebooking.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a booking
 * 
 * CRITICAL: This entity uses optimistic locking (@Version) to prevent concurrent booking conflicts
 * 
 * Booking Flow:
 * 1. Create booking with PENDING status
 * 2. Lock seats by creating BookingSeat records
 * 3. Process payment
 * 4. Update status to CONFIRMED
 * 
 * Concurrency Handling:
 * - @Version annotation enables optimistic locking
 * - If two users try to book same seats, one will get OptimisticLockException
 * - Database unique constraint on (show_id, seat_id) provides additional safety
 */
@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal discountAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PaymentMethod paymentMethod;
    
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BookingSeat> bookingSeats = new ArrayList<>();
    
    /**
     * CRITICAL: Optimistic locking version field
     * 
     * This field is automatically managed by JPA:
     * - Incremented on each update
     * - Checked before update to detect concurrent modifications
     * - If version mismatch, OptimisticLockException is thrown
     * 
     * This prevents double booking scenarios where two users
     * try to book the same seats simultaneously
     */
    @Version
    private Integer version;
    
    /**
     * Helper method to add a booking seat
     * Maintains bidirectional relationship
     */
    public void addBookingSeat(BookingSeat bookingSeat) {
        bookingSeats.add(bookingSeat);
        bookingSeat.setBooking(this);
    }
}
