package com.moviebooking.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a user in the system
 * 
 * Users can browse shows and create bookings
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
}
