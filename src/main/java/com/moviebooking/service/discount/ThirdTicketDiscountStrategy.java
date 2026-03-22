package com.moviebooking.service.discount;

import com.moviebooking.entity.BookingSeat;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 50% discount on 3rd ticket
 * 
 * Applied when booking has 3 or more seats
 * Discount is calculated on the cheapest seat
 */
@Component
public class ThirdTicketDiscountStrategy implements DiscountStrategy {
    
    private static final BigDecimal DISCOUNT_PERCENTAGE = new BigDecimal("0.50");
    
    @Override
    public BigDecimal calculateDiscount(BookingContext context) {
        if (!isApplicable(context)) {
            return BigDecimal.ZERO;
        }
        
        // Get the price of the cheapest seat (3rd ticket)
        BigDecimal thirdTicketPrice = context.getBooking().getBookingSeats().stream()
            .map(BookingSeat::getPrice)
            .sorted()
            .skip(2)
            .findFirst()
            .orElse(BigDecimal.ZERO);
        
        return thirdTicketPrice.multiply(DISCOUNT_PERCENTAGE);
    }
    
    @Override
    public boolean isApplicable(BookingContext context) {
        return context.getSeatCount() >= 3;
    }
    
    @Override
    public String getDiscountName() {
        return "Third Ticket 50% Off";
    }
}
