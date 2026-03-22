package com.moviebooking.service;

import com.moviebooking.entity.Booking;
import com.moviebooking.exception.PaymentFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Mock payment service
 * 
 * In production, this would integrate with actual payment gateways
 * like Razorpay, Stripe, etc.
 */
@Service
@Slf4j
public class PaymentService {
    
    /**
     * Process payment (mock implementation)
     * 
     * In production:
     * - Call payment gateway API
     * - Handle webhooks for async confirmation
     * - Implement retry logic
     * - Store transaction details
     * 
     * @param booking Booking entity
     * @param amount Amount to charge
     * @return Transaction ID
     */
    public String processPayment(Booking booking, BigDecimal amount) {
        log.info("Processing payment for booking: {}, amount: {}", booking.getId(), amount);
        
        // Mock payment processing
        String transactionId = UUID.randomUUID().toString();
        
        // Simulate payment success (90% success rate for testing)
        if (Math.random() > 0.9) {
            log.error("Payment failed for booking: {}", booking.getId());
            throw new PaymentFailedException("Payment gateway error", transactionId);
        }
        
        log.info("Payment successful. Transaction ID: {}", transactionId);
        return transactionId;
    }
}
