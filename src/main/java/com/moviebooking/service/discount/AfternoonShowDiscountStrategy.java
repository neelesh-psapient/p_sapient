package com.moviebooking.service.discount;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * 20% discount for afternoon shows (12 PM - 3 PM)
 * 
 * Applied when show start time is between 12:00 and 15:00
 */
@Component
public class AfternoonShowDiscountStrategy implements DiscountStrategy {
    
    private static final LocalTime AFTERNOON_START = LocalTime.of(12, 0);
    private static final LocalTime AFTERNOON_END = LocalTime.of(15, 0);
    private static final BigDecimal DISCOUNT_PERCENTAGE = new BigDecimal("0.20");
    
    @Override
    public BigDecimal calculateDiscount(BookingContext context) {
        if (!isApplicable(context)) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal totalAmount = context.getBooking().getTotalAmount();
        return totalAmount.multiply(DISCOUNT_PERCENTAGE);
    }
    
    @Override
    public boolean isApplicable(BookingContext context) {
        LocalTime showTime = context.getShow().getStartTime().toLocalTime();
        return !showTime.isBefore(AFTERNOON_START) && showTime.isBefore(AFTERNOON_END);
    }
    
    @Override
    public String getDiscountName() {
        return "Afternoon Show 20% Off";
    }
}
