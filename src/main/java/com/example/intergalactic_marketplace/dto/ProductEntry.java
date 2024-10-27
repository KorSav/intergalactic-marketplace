package com.example.intergalactic_marketplace.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class ProductEntry {
    UUID id;
    String name;
    String description;
    int price;
    String category;
}
