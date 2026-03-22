package com.moviebooking.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a screen within a theatre
 * 
 * Each screen belongs to a theatre and has multiple seats
 * Shows are scheduled on screens
 */
@Entity
@Table(name = "screens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Screen extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theatre_id", nullable = false)
    private Theatre theatre;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private Integer totalSeats;
}
