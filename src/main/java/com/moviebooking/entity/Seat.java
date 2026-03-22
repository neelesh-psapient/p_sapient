package com.moviebooking.entity;

import com.moviebooking.enums.SeatType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entity representing a seat in a screen
 * 
 * Each seat has a unique seat number within a screen
 * Seats have different types (REGULAR, PREMIUM, VIP) with different pricing
 */
@Entity
@Table(name = "seats", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"screen_id", "seat_number"}),
       indexes = @Index(name = "idx_seat_screen", columnList = "screen_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;
    
    @Column(nullable = false, length = 10)
    private String seatNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeatType seatType;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
}
