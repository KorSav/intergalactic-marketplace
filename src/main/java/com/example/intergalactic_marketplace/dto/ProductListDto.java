package com.example.intergalactic_marketplace.dto;

import java.util.List;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class ProductListDto {
    List<ProductEntry> products;
}
