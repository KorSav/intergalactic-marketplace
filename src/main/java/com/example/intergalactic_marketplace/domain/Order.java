package com.example.intergalactic_marketplace.domain;

import java.util.UUID;
import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Order {
    UUID id;
    String status;

    Customer customer;
    List<Product> products;
}
