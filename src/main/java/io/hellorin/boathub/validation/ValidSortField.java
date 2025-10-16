package io.hellorin.boathub.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation to ensure sortBy field contains only valid values.
 * Valid values are: id, name, description, boatType
 */
@Documented
@Constraint(validatedBy = ValidSortFieldValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSortField {
    String message() default "Invalid sortBy field. Allowed values are: id, name, description, boatType";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
