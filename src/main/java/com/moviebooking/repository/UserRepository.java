package com.moviebooking.repository;

import com.moviebooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email
     * @param email User email
     * @return Optional containing user if found
     */
    Optional<User> findByEmail(String email);
}
