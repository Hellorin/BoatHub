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

    @Test
    void isValid_WithValidAscDirection_ShouldReturnTrue() {
        // Given
        TestSortDirectionDto dto = new TestSortDirectionDto("asc");

        // When
        Set<ConstraintViolation<TestSortDirectionDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Valid 'asc' direction should not produce violations").isEmpty();
    }

    @Test
    void isValid_WithValidDescDirection_ShouldReturnTrue() {
        // Given
        TestSortDirectionDto dto = new TestSortDirectionDto("desc");

        // When
        Set<ConstraintViolation<TestSortDirectionDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Valid 'desc' direction should not produce violations").isEmpty();
    }

    @Test
    void isValid_WithUpperCaseAscDirection_ShouldReturnTrue() {
        // Given
        TestSortDirectionDto dto = new TestSortDirectionDto("ASC");

        // When
        Set<ConstraintViolation<TestSortDirectionDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Uppercase 'ASC' direction should be accepted").isEmpty();
    }

    @Test
    void isValid_WithUpperCaseDescDirection_ShouldReturnTrue() {
        // Given
        TestSortDirectionDto dto = new TestSortDirectionDto("DESC");

        // When
        Set<ConstraintViolation<TestSortDirectionDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Uppercase 'DESC' direction should be accepted").isEmpty();
    }

    @Test
    void isValid_WithMixedCaseAscDirection_ShouldReturnTrue() {
        // Given
        TestSortDirectionDto dto = new TestSortDirectionDto("Asc");

        // When
        Set<ConstraintViolation<TestSortDirectionDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Mixed case 'Asc' direction should be accepted").isEmpty();
    }

    @Test
    void isValid_WithMixedCaseDescDirection_ShouldReturnTrue() {
        // Given
        TestSortDirectionDto dto = new TestSortDirectionDto("Desc");

        // When
        Set<ConstraintViolation<TestSortDirectionDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Mixed case 'Desc' direction should be accepted").isEmpty();
    }

    @Test
    void isValid_WithInvalidDirection_ShouldReturnFalse() {
        // Given
        TestSortDirectionDto dto = new TestSortDirectionDto("invalid");

        // When
        Set<ConstraintViolation<TestSortDirectionDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Invalid direction should produce violations").isNotEmpty();
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Invalid sortDirection");
    }

    @Test
    void isValid_WithAnotherInvalidDirection_ShouldReturnFalse() {
        // Given
        TestSortDirectionDto dto = new TestSortDirectionDto("ascending");

        // When
        Set<ConstraintViolation<TestSortDirectionDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Invalid direction should produce violations").isNotEmpty();
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Invalid sortDirection");
    }

    @Test
    void isValid_WithEmptyStringDirection_ShouldReturnFalse() {
        // Given
        TestSortDirectionDto dto = new TestSortDirectionDto("");

        // When
        Set<ConstraintViolation<TestSortDirectionDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Empty string should be invalid").isNotEmpty();
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage().contains("Invalid sortDirection"));
    }

    @Test
    void isValid_WithWhitespaceOnlyDirection_ShouldReturnFalse() {
        // Given
        TestSortDirectionDto dto = new TestSortDirectionDto("   ");

        // When
        Set<ConstraintViolation<TestSortDirectionDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Whitespace-only string should be invalid").isNotEmpty();
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage().contains("Invalid sortDirection"));
    }

    @Test
    void isValid_WithNullDirection_ShouldReturnTrue() {
        // Given
        TestSortDirectionDto dto = new TestSortDirectionDto(null);

        // When
        Set<ConstraintViolation<TestSortDirectionDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Null values should be handled by @NotNull, not @ValidSortDirection").isEmpty();
    }

    @Test
    void isValid_WithValidDirectionValues_ShouldReturnTrue() {
        // Given - Test the validator directly with all valid direction values
        ValidSortDirectionValidator validatorImpl = new ValidSortDirectionValidator();
        validatorImpl.initialize(null);

        // When & Then - Test all valid directions
        String[] validDirections = {"asc", "desc", "ASC", "DESC", "Asc", "Desc"};
        for (String direction : validDirections) {
            boolean isValid = validatorImpl.isValid(direction, null);
            assertThat(isValid).as("Direction '%s' should be valid", direction).isTrue();
        }
    }

    @Test
    void isValid_WithInvalidDirectionValues_ShouldReturnFalse() {
        // Given - Test the validator directly with invalid direction values
        ValidSortDirectionValidator validatorImpl = new ValidSortDirectionValidator();
        validatorImpl.initialize(null);

        // When & Then - Test invalid directions
        String[] invalidDirections = {"", "invalid", "ascending", "descending", "up", "down", "1", "0"};
        for (String direction : invalidDirections) {
            boolean isValid = validatorImpl.isValid(direction, null);
            assertThat(isValid).as("Direction '%s' should be invalid", direction).isFalse();
        }
    }

    @Test
    void isValid_WithNumericDirection_ShouldReturnFalse() {
        // Given
        TestSortDirectionDto dto = new TestSortDirectionDto("1");

        // When
        Set<ConstraintViolation<TestSortDirectionDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Numeric direction should be invalid").isNotEmpty();
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage().contains("Invalid sortDirection"));
    }

    @Test
    void isValid_WithSpecialCharactersDirection_ShouldReturnFalse() {
        // Given
        TestSortDirectionDto dto = new TestSortDirectionDto("asc@");

        // When
        Set<ConstraintViolation<TestSortDirectionDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Direction with special characters should be invalid").isNotEmpty();
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage().contains("Invalid sortDirection"));
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
