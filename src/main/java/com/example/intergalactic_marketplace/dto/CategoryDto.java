package com.example.intergalactic_marketplace.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class CategoryDto {
    int id;
    String name;
}
