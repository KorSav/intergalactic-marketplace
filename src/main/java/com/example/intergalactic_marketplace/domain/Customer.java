package com.example.intergalactic_marketplace.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Customer {
    Long id;
    String name;
    String address;
    String email;
}
