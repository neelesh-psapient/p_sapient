package com.moviebooking.entity;

import com.moviebooking.enums.ShowStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing a movie show
 * 
 * A show is a scheduled screening of a movie on a specific screen
 * Shows have start and end times and can be in different statuses
 */
@Entity
@Table(name = "shows", indexes = {
    @Index(name = "idx_show_movie_screen", columnList = "movie_id,screen_id"),
    @Index(name = "idx_show_start_time", columnList = "start_time")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Show extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;
    
    @Column(nullable = false)
    private LocalDateTime startTime;
    
    @Column(nullable = false)
    private LocalDateTime endTime;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ShowStatus status = ShowStatus.ACTIVE;
}
