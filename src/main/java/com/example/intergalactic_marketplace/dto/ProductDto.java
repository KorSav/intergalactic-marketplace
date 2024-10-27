package com.example.intergalactic_marketplace.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class ProductDto {
    String name;
    String description;
    int price;
    CategoryDto category;
}
