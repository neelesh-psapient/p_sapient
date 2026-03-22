package com.moviebooking.repository;

import com.moviebooking.entity.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Theatre entity
 */
@Repository
public interface TheatreRepository extends JpaRepository<Theatre, Long> {
    
    /**
     * Find theatres by city
     * @param city City name
     * @return List of theatres in the city
     */
    List<Theatre> findByCity(String city);
}
