package com.example.intergalactic_marketplace.service.exception;

import java.util.UUID;

public class CustomerHasNoRulesToUpdateProductException extends RuntimeException {
    
    private static final String baseMessage = "Customer with id %d has no rules to update product with id %s";

    public CustomerHasNoRulesToUpdateProductException(Long customerId, UUID productId){
        super(String.format(baseMessage, customerId, productId));
    }
    
}