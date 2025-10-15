package io.hellorin.boathub.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    @ParameterizedTest
    @ValueSource(strings = {"id", "name", "description", "boatType"})
    void isValid_WithValidSortFields_ShouldReturnTrue(String sortField) {
        // Given
        TestSortFieldDto dto = new TestSortFieldDto(sortField);

        // When
        Set<ConstraintViolation<TestSortFieldDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Sort field '%s' should be valid", sortField).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalidField", "createdAt", "updatedAt", "price", "ID", "NAME", "   "})
    void isValid_WithInvalidSortFields_ShouldReturnFalse(String sortField) {
        // Given
        TestSortFieldDto dto = new TestSortFieldDto(sortField);

        // When
        Set<ConstraintViolation<TestSortFieldDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Sort field '%s' should be invalid", sortField).isNotEmpty().hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Invalid sortBy field");
    }

    @Test
    void isValid_WithEmptySortField_ShouldReturnFalse() {
        // Given
        TestSortFieldDto dto = new TestSortFieldDto("");

        // When
        Set<ConstraintViolation<TestSortFieldDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Empty sort field should be invalid").isNotEmpty().hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Invalid sortBy field");
    }

    @Test
    void isValid_WithNullSortField_ShouldReturnTrue() {
        // Given
        TestSortFieldDto dto = new TestSortFieldDto(null);

        // When
        Set<ConstraintViolation<TestSortFieldDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Null sort field should be valid (handled by @NotNull)").isEmpty();
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
