package com.moviebooking.repository;

import com.moviebooking.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Movie entity
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    
    /**
     * Find movies by language
     * @param language Movie language
     * @return List of movies
     */
    List<Movie> findByLanguage(String language);
    
    /**
     * Find movies by genre
     * @param genre Movie genre
     * @return List of movies
     */
    List<Movie> findByGenre(String genre);
}
