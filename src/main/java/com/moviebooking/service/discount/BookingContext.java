package com.moviebooking.service.discount;

import com.moviebooking.entity.Booking;
import com.moviebooking.entity.Show;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingContext {
    private Booking booking;
    private Show show;
    private Integer seatCount;
}
