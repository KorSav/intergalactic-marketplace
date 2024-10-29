package com.example.intergalactic_marketplace.service.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {
    private static final String baseMessage = "Product with id {} not found";

    public ProductNotFoundException(UUID id){
        super(String.format(baseMessage, id));
    }
}
