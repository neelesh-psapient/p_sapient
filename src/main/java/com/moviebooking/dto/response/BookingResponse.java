package com.moviebooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private String bookingId;
    private String status;
    private BigDecimal amount;
    private BigDecimal discount;
    private List<String> seats;
}
