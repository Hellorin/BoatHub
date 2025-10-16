package io.hellorin.boathub.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation to ensure sortDirection field contains only valid values.
 * Valid values are: asc, desc
 */
@Documented
@Constraint(validatedBy = ValidSortDirectionValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSortDirection {
    String message() default "Invalid sortDirection. Allowed values are: asc, desc";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
