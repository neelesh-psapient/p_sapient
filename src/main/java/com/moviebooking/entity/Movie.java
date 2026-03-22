package com.moviebooking.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a movie
 * 
 * Movies are shown in theatres and can have multiple shows
 */
@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie extends BaseEntity {
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String language;
    
    @Column(nullable = false)
    private String genre;
}
