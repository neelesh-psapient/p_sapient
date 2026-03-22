package com.moviebooking.repository;

import com.moviebooking.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Seat entity
 */
@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    
    /**
     * Find seats by screen ID
     * @param screenId Screen ID
     * @return List of seats in the screen
     */
    List<Seat> findByScreenId(Long screenId);
    
    /**
     * Find seats by screen ID and seat numbers
     * Used for validating seat selection during booking
     * 
     * @param screenId Screen ID
     * @param seatNumbers List of seat numbers
     * @return List of matching seats
     */
    @Query("SELECT s FROM Seat s WHERE s.screen.id = :screenId AND s.seatNumber IN :seatNumbers")
    List<Seat> findByScreenAndSeatNumbers(
        @Param("screenId") Long screenId,
        @Param("seatNumbers") List<String> seatNumbers
    );
}
