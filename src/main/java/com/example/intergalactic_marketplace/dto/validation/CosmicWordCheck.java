package com.example.intergalactic_marketplace.dto.validation;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.TYPE_USE, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CosmicWordChecker.class)
@Documented
public @interface CosmicWordCheck {
    String message() default "Invalid product name, ensure it contains cosmic words, like: star, galaxy, comet";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
