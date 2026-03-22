package com.moviebooking.controller;

import com.moviebooking.dto.response.ShowResponse;
import com.moviebooking.service.ShowService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controller for show-related endpoints
 */
@RestController
@RequestMapping("/api/v1/shows")
@RequiredArgsConstructor
@Slf4j
public class ShowController {
    
    private final ShowService showService;
    
    /**
     * Browse shows by city, movie, and date
     * 
     * @param city City name (required)
     * @param movieId Movie ID (required)
     * @param date Date in yyyy-MM-dd format (required)
     * @return List of shows with available seats
     */
    @GetMapping
    public ResponseEntity<ShowResponse> browseShows(
            @RequestParam @NotBlank String city,
            @RequestParam @NotNull Long movieId,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        log.info("GET /api/v1/shows - city: {}, movieId: {}, date: {}", city, movieId, date);
        
        ShowResponse response = showService.browseShows(city, movieId, date);
        return ResponseEntity.ok(response);
    }
}
