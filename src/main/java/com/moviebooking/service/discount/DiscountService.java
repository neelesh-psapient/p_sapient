package com.moviebooking.service.discount;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service for calculating discounts using Strategy Pattern
 * 
 * Automatically discovers all DiscountStrategy implementations
 * and applies applicable ones
 */
@Service
@RequiredArgsConstructor
public class DiscountService {
    
    private final List<DiscountStrategy> discountStrategies;
    
    /**
     * Calculate total discount by applying all applicable strategies
     * Strategies are applied independently and discounts are summed
     * 
     * @param context Booking context
     * @return Total discount amount
     */
    public BigDecimal calculateTotalDiscount(BookingContext context) {
        return discountStrategies.stream()
            .filter(strategy -> strategy.isApplicable(context))
            .map(strategy -> strategy.calculateDiscount(context))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
