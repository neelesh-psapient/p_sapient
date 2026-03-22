package com.moviebooking.service.discount;

import java.math.BigDecimal;

/**
 * Strategy interface for discount calculation
 * 
 * Implementations should be annotated with @Component
 * to be automatically registered with Spring
 */
public interface DiscountStrategy {
    
    /**
     * Calculate discount for the given booking context
     * @param context Booking context containing booking, show, and seat count
     * @return Discount amount
     */
    BigDecimal calculateDiscount(BookingContext context);
    
    /**
     * Check if this discount strategy is applicable
     * @param context Booking context
     * @return true if applicable, false otherwise
     */
    boolean isApplicable(BookingContext context);
    
    /**
     * Get the name of this discount strategy
     * @return Discount name
     */
    String getDiscountName();
}
