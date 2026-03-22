package com.moviebooking.exception;

import lombok.Getter;

@Getter
public class PaymentFailedException extends RuntimeException {
    private final String transactionId;
    
    public PaymentFailedException(String message, String transactionId) {
        super(message);
        this.transactionId = transactionId;
    }
    
    public PaymentFailedException(String message) {
        super(message);
        this.transactionId = null;
    }
}
