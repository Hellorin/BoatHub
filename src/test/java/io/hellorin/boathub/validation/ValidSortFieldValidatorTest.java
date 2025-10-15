package io.hellorin.boathub.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ValidSortFieldValidator.
 * Tests validation of sort field values to ensure only valid field names are accepted.
 */
class ValidSortFieldValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void isValid_WithValidSortField_ShouldReturnTrue() {
        // Given
        TestSortFieldDto dto = new TestSortFieldDto("id");

        // When
        Set<ConstraintViolation<TestSortFieldDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Valid sort field should not produce violations").isEmpty();
    }

    @Test
    void isValid_WithAllValidSortFields_ShouldReturnTrue() {
        // Given & When & Then
        String[] validFields = {"id", "name", "description", "boatType"};
        
        for (String field : validFields) {
            TestSortFieldDto dto = new TestSortFieldDto(field);
            Set<ConstraintViolation<TestSortFieldDto>> violations = validator.validate(dto);
            assertThat(violations)
                .as("Sort field '%s' should be valid but validation failed", field).isEmpty();
        }
    }

    @Test
    void isValid_WithInvalidSortField_ShouldReturnFalse() {
        // Given
        TestSortFieldDto dto = new TestSortFieldDto("invalidField");

        // When
        Set<ConstraintViolation<TestSortFieldDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Invalid sort field should produce violations").isNotEmpty();
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Invalid sortBy field");
    }

    @Test
    void isValid_WithAnotherInvalidSortField_ShouldReturnFalse() {
        // Given
        TestSortFieldDto dto = new TestSortFieldDto("createdAt");

        // When
        Set<ConstraintViolation<TestSortFieldDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Invalid sort field should produce violations").isNotEmpty();
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Invalid sortBy field");
    }

    @Test
    void isValid_WithNullSortField_ShouldReturnTrue() {
        // Given
        TestSortFieldDto dto = new TestSortFieldDto(null);

        // When
        Set<ConstraintViolation<TestSortFieldDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Null values should be handled by @NotNull, not @ValidSortField").isEmpty();
    }

    @Test
    void isValid_WithEmptyStringSortField_ShouldReturnFalse() {
        // Given
        TestSortFieldDto dto = new TestSortFieldDto("");

        // When
        Set<ConstraintViolation<TestSortFieldDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Empty string should be invalid").isNotEmpty();
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Invalid sortBy field");
    }

    @Test
    void isValid_WithWhitespaceOnlySortField_ShouldReturnFalse() {
        // Given
        TestSortFieldDto dto = new TestSortFieldDto("   ");

        // When
        Set<ConstraintViolation<TestSortFieldDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Whitespace-only string should be invalid").isNotEmpty();
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Invalid sortBy field");
    }

    @Test
    void isValid_WithCaseSensitiveInvalidField_ShouldReturnFalse() {
        // Given
        TestSortFieldDto dto = new TestSortFieldDto("ID"); // uppercase

        // When
        Set<ConstraintViolation<TestSortFieldDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Case-sensitive validation should reject uppercase field").isNotEmpty();
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Invalid sortBy field");
    }

    @Test
    void isValid_WithValidSortFieldValues_ShouldReturnTrue() {
        // Given - Test the validator directly with all valid field values
        ValidSortFieldValidator validatorImpl = new ValidSortFieldValidator();
        validatorImpl.initialize(null);

        // When & Then - Test all valid sort fields
        String[] validFields = {"id", "name", "description", "boatType"};
        for (String field : validFields) {
            boolean isValid = validatorImpl.isValid(field, null);
            assertThat(isValid).as("Sort field '%s' should be valid", field).isTrue();
        }
    }

    @Test
    void isValid_WithInvalidSortFieldValues_ShouldReturnFalse() {
        // Given - Test the validator directly with invalid field values
        ValidSortFieldValidator validatorImpl = new ValidSortFieldValidator();
        validatorImpl.initialize(null);

        // When & Then - Test invalid sort fields
        String[] invalidFields = {"", "invalid", "ID", "createdAt", "updatedAt", "price"};
        for (String field : invalidFields) {
            boolean isValid = validatorImpl.isValid(field, null);
            assertThat(isValid).as("Sort field '%s' should be invalid", field).isFalse();
        }
    }

    /**
     * Test DTO class to test the ValidSortField annotation.
     */
    private static class TestSortFieldDto {
        
        @ValidSortField
        private final String sortField;
        
        public TestSortFieldDto(String sortField) {
            this.sortField = sortField;
        }
    }
}
