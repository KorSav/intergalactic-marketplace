package com.example.intergalactic_marketplace.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class CosmicWordChecker implements ConstraintValidator<CosmicWordCheck, String> {
    
    private final List<String> cosmicWords = List.of(
        "star", "galaxy", "comet"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        String lowerCaseValue = value;
        return cosmicWords.stream()
            .map(String::toLowerCase)
            .anyMatch(lowerCaseValue::contains);
    }
    
}
