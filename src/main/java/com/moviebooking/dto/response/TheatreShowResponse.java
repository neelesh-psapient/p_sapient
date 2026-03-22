package com.moviebooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TheatreShowResponse {
    private String theatreId;
    private String theatreName;
    private String showId;
    private String time;
    private Integer availableSeats;
}
