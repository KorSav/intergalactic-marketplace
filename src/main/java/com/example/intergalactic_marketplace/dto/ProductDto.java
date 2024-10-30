package com.example.intergalactic_marketplace.dto;

import com.example.intergalactic_marketplace.dto.validation.CosmicWordCheck;
import com.example.intergalactic_marketplace.dto.validation.ExtendedValidation;

import jakarta.validation.GroupSequence;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@GroupSequence({ProductDto.class, ExtendedValidation.class})
public class ProductDto {

    @NotBlank(message = "Product name could not be blank")
    @Size(min = 5, message = "Product name could not be shorter than 5 characters")
    @Size(max = 30, message = "Product name could not be longer than 30 characters")
    @CosmicWordCheck(groups = ExtendedValidation.class)
    String name;

    @NotBlank(message = "Product description could not be blank")
    @Size(max = 500, message = "Product description could not be longer than 500 characters")
    @Size(min = 5, message = "Product description could not be shorter than 5 characters")
    String description;

    @Max(value = 100_000, message = "Price could not exceed 100 000")
    @Min(value = 1, message = "Price could not be less than 1")
    int price;
    
    @Valid
    CategoryDto category;
}
