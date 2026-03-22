package com.moviebooking.repository;

import com.moviebooking.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Show entity
 * 
 * Contains custom queries for browsing shows and fetching with relationships
 */
@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
    
    /**
     * Find shows by city, movie, and date for browse functionality
     * Uses JOIN FETCH to avoid N+1 queries
     * 
     * This is the core query for the Browse Shows API
     * 
     * @param city City name
     * @param movieId Movie ID
     * @param date Date to search for shows
     * @return List of shows matching the criteria
     */
    @Query("""
        SELECT DISTINCT s FROM Show s
        JOIN FETCH s.movie m
        JOIN FETCH s.screen sc
        JOIN FETCH sc.theatre t
        WHERE t.city = :city
        AND m.id = :movieId
        AND DATE(s.startTime) = :date
        AND s.status = 'ACTIVE'
        ORDER BY s.startTime
        """)
    List<Show> findShowsByCityMovieAndDate(
        @Param("city") String city,
        @Param("movieId") Long movieId,
        @Param("date") LocalDate date
    );
    
    /**
     * Find show with all necessary relationships for booking
     * Eagerly fetches screen and theatre to avoid lazy loading issues
     * 
     * @param showId Show ID
     * @return Optional containing show with relationships
     */
    @Query("""
        SELECT s FROM Show s
        JOIN FETCH s.screen sc
        JOIN FETCH sc.theatre t
        JOIN FETCH s.movie m
        WHERE s.id = :showId
        """)
    Optional<Show> findByIdWithRelationships(@Param("showId") Long showId);
}
