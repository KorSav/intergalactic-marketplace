package com.example.intergalactic_marketplace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class CategoryDto {
    int id;
    
    @NotBlank(message = "Category name could not be blank")
    @Size(min = 5, message = "Category name could not be shorter than 5 characters")
    @Size(max = 30, message = "Category name could not be longer than 30 characters")
    String name;
}
