package io.hellorin.boathub.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for ValidSortDirection annotation.
 * Validates that the sortDirection field contains only allowed values.
 */
public class ValidSortDirectionValidator implements ConstraintValidator<ValidSortDirection, String> {
    
    @Override
    public void initialize(ValidSortDirection constraintAnnotation) {
        // No initialization needed
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null validation
        }
        
        return "asc".equalsIgnoreCase(value) || "desc".equalsIgnoreCase(value);
    }
}
