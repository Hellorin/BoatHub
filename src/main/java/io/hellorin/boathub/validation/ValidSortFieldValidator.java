package io.hellorin.boathub.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for ValidSortField annotation.
 * Validates that the sortBy field contains only allowed values.
 */
public class ValidSortFieldValidator implements ConstraintValidator<ValidSortField, String> {
    
    private static final String[] VALID_FIELDS = {"id", "name", "description", "boatType"};
    
    @Override
    public void initialize(ValidSortField constraintAnnotation) {
        // No initialization needed
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null validation
        }
        
        for (String validField : VALID_FIELDS) {
            if (validField.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
