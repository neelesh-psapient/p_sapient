package com.moviebooking.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a theatre/cinema
 * 
 * Theatres contain multiple screens and are located in specific cities
 */
@Entity
@Table(name = "theatres", indexes = {
    @Index(name = "idx_theatre_city", columnList = "city")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Theatre extends BaseEntity {
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String city;
    
    @Column(columnDefinition = "TEXT")
    private String address;
}
