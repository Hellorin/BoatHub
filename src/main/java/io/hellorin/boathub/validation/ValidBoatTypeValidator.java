package io.hellorin.boathub.validation;

import io.hellorin.boathub.domain.BoatType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for ValidBoatType annotation.
 * Validates that the provided String represents a valid BoatType enum value.
 */
public class ValidBoatTypeValidator implements ConstraintValidator<ValidBoatType, String> {
    
    @Override
    public void initialize(ValidBoatType constraintAnnotation) {
        // No initialization needed
    }
    
    @Override
    public boolean isValid(String boatType, ConstraintValidatorContext context) {
        // Null and empty values are handled by @NotBlank annotation
        if (boatType == null || boatType.trim().isEmpty()) {
            return true;
        }
        
        // Check if the string represents a valid boat type enum value
        // Trim the input to handle spaces before and after the boat type
        try {
            BoatType.valueOf(boatType.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
