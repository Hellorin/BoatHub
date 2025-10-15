package io.hellorin.boathub.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation to ensure that a BoatType value is valid.
 * Validates that the provided boat type exists in the BoatType enum.
 */
@Documented
@Constraint(validatedBy = ValidBoatTypeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBoatType {
    
    String message() default "Invalid boat type. Must be one of: SAILBOAT, MOTORBOAT, YACHT, SPEEDBOAT, FISHING_BOAT, OTHER";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
