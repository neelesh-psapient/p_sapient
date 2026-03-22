package com.moviebooking.service;

import com.moviebooking.dto.response.ShowResponse;
import com.moviebooking.dto.response.TheatreShowResponse;
import com.moviebooking.entity.Show;
import com.moviebooking.repository.BookingSeatRepository;
import com.moviebooking.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for show-related operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ShowService {
    
    private final ShowRepository showRepository;
    private final BookingSeatRepository bookingSeatRepository;
    
    /**
     * Browse shows by city, movie, and date
     * Returns shows with available seat counts
     * 
     * @param city City name
     * @param movieId Movie ID
     * @param date Date to search for shows
     * @return ShowResponse containing list of shows
     */
    @Transactional(readOnly = true)
    public ShowResponse browseShows(String city, Long movieId, LocalDate date) {
        log.info("Browsing shows for city: {}, movie: {}, date: {}", city, movieId, date);
        
        List<Show> shows = showRepository.findShowsByCityMovieAndDate(city, movieId, date);
        
        if (shows.isEmpty()) {
            log.info("No shows found for the given criteria");
            return ShowResponse.builder()
                .movie("")
                .city(city)
                .shows(Collections.emptyList())
                .build();
        }
        
        // Calculate available seats for each show
        List<TheatreShowResponse> showResponses = shows.stream()
            .map(this::mapToTheatreShowResponse)
            .collect(Collectors.toList());
        
        return ShowResponse.builder()
            .movie(shows.get(0).getMovie().getName())
            .city(city)
            .shows(showResponses)
            .build();
    }
    
    private TheatreShowResponse mapToTheatreShowResponse(Show show) {
        Integer availableSeats = bookingSeatRepository.countAvailableSeats(
            show.getId(), show.getScreen().getId());
        
        return TheatreShowResponse.builder()
            .theatreId(show.getScreen().getTheatre().getId().toString())
            .theatreName(show.getScreen().getTheatre().getName())
            .showId(show.getId().toString())
            .time(show.getStartTime().toLocalTime().toString())
            .availableSeats(availableSeats)
            .build();
    }
}
