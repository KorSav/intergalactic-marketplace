package com.example.intergalactic_marketplace.service.exception;

import java.util.UUID;

public class CustomerHasNoRulesToDeleteProductException extends RuntimeException {
    private static final String baseMessage = "Customer with id {}, has no rules to delete product with id {}";

    public CustomerHasNoRulesToDeleteProductException(Long customerId, UUID productId){
        super(String.format(baseMessage, customerId, productId));
    }
}
