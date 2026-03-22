package com.moviebooking.repository;

import com.moviebooking.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Screen entity
 */
@Repository
public interface ScreenRepository extends JpaRepository<Screen, Long> {
    
    /**
     * Find screens by theatre ID
     * @param theatreId Theatre ID
     * @return List of screens in the theatre
     */
    List<Screen> findByTheatreId(Long theatreId);
}
