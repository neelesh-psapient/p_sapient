package com.moviebooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowResponse {
    private String movie;
    private String city;
    private List<TheatreShowResponse> shows;
}
