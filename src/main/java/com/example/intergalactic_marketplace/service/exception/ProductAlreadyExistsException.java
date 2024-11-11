package com.example.intergalactic_marketplace.service.exception;

public class ProductAlreadyExistsException extends RuntimeException {
  private static final String baseMessage = "Product with name %s already exists";

  public ProductAlreadyExistsException(String name) {
    super(String.format(baseMessage, name));
  }
}
