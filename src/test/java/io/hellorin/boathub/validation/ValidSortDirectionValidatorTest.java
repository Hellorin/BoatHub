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
 * Unit tests for ValidSortDirectionValidator.
 * Tests validation of sort direction values to ensure only valid direction values are accepted.
 */
class ValidSortDirectionValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {"asc", "desc", "ASC", "DESC", "Asc", "Desc"})
    void isValid_WithValidDirections_ShouldReturnTrue(String direction) {
        // Given
        TestSortDirectionDto dto = new TestSortDirectionDto(direction);

        // When
        Set<ConstraintViolation<TestSortDirectionDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Direction '%s' should be valid", direction).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "ascending", "descending", "up", "down", "1", "0", "asc@", "   "})
    void isValid_WithInvalidDirections_ShouldReturnFalse(String direction) {
        // Given
        TestSortDirectionDto dto = new TestSortDirectionDto(direction);

        // When
        Set<ConstraintViolation<TestSortDirectionDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Direction '%s' should be invalid", direction).isNotEmpty().hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Invalid sortDirection");
    }

    @Test
    void isValid_WithEmptyDirection_ShouldReturnFalse() {
        // Given
        TestSortDirectionDto dto = new TestSortDirectionDto("");

        // When
        Set<ConstraintViolation<TestSortDirectionDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Empty direction should be invalid").isNotEmpty().hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Invalid sortDirection");
    }

    @Test
    void isValid_WithNullDirection_ShouldReturnTrue() {
        // Given
        TestSortDirectionDto dto = new TestSortDirectionDto(null);

        // When
        Set<ConstraintViolation<TestSortDirectionDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Null direction should be valid (handled by @NotNull)").isEmpty();
    }


    /**
     * Test DTO class to test the ValidSortDirection annotation.
     */
    private static class TestSortDirectionDto {
        
        @ValidSortDirection
        private final String sortDirection;
        
        public TestSortDirectionDto(String sortDirection) {
            this.sortDirection = sortDirection;
        }
    }
}
