package io.hellorin.boathub.validation;

import io.hellorin.boathub.domain.BoatType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidBoatTypeValidator.
 * Tests validation of boat type string values to ensure only valid enum values are accepted.
 */
class ValidBoatTypeValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void isValid_WithValidBoatType_ShouldReturnTrue() {
        // Given
        TestBoatTypeDto dto = new TestBoatTypeDto("SAILBOAT");

        // When
        Set<ConstraintViolation<TestBoatTypeDto>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty(), "Valid boat type should not produce violations");
    }

    @Test
    void isValid_WithAllValidBoatTypes_ShouldReturnTrue() {
        // Given & When & Then
        for (BoatType boatType : BoatType.values()) {
            TestBoatTypeDto dto = new TestBoatTypeDto(boatType.name());
            Set<ConstraintViolation<TestBoatTypeDto>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty(), 
                "BoatType " + boatType + " should be valid but validation failed");
        }
    }

    @Test
    void isValid_WithLowerCaseValidBoatType_ShouldReturnTrue() {
        // Given
        TestBoatTypeDto dto = new TestBoatTypeDto("sailboat");

        // When
        Set<ConstraintViolation<TestBoatTypeDto>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty(), "Lowercase valid boat type should be accepted");
    }

    @Test
    void isValid_WithMixedCaseValidBoatType_ShouldReturnTrue() {
        // Given
        TestBoatTypeDto dto = new TestBoatTypeDto("MotorBoat");

        // When
        Set<ConstraintViolation<TestBoatTypeDto>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty(), "Mixed case valid boat type should be accepted");
    }

    @Test
    void isValid_WithInvalidBoatType_ShouldReturnFalse() {
        // Given
        TestBoatTypeDto dto = new TestBoatTypeDto("SAILBOAT2");

        // When
        Set<ConstraintViolation<TestBoatTypeDto>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty(), "Invalid boat type should produce violations");
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Invalid boat type"));
    }

    @Test
    void isValid_WithAnotherInvalidBoatType_ShouldReturnFalse() {
        // Given
        TestBoatTypeDto dto = new TestBoatTypeDto("INVALID_TYPE");

        // When
        Set<ConstraintViolation<TestBoatTypeDto>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty(), "Invalid boat type should produce violations");
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Invalid boat type"));
    }

    @Test
    void isValid_WithNullBoatType_ShouldReturnTrue() {
        // Given
        TestBoatTypeDto dto = new TestBoatTypeDto(null);

        // When
        Set<ConstraintViolation<TestBoatTypeDto>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty(), "Null values should be handled by @NotBlank, not @ValidBoatType");
    }

    @Test
    void isValid_WithEmptyStringBoatType_ShouldReturnTrue() {
        // Given
        TestBoatTypeDto dto = new TestBoatTypeDto("");

        // When
        Set<ConstraintViolation<TestBoatTypeDto>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty(), "Empty string should be handled by @NotBlank, not @ValidBoatType");
    }

    @Test
    void isValid_WithWhitespaceOnlyBoatType_ShouldReturnTrue() {
        // Given
        TestBoatTypeDto dto = new TestBoatTypeDto("   ");

        // When
        Set<ConstraintViolation<TestBoatTypeDto>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty(), "Whitespace-only string should be handled by @NotBlank, not @ValidBoatType");
    }

    @Test
    void isValid_WithValidBoatTypeValues_ShouldReturnTrue() {
        // Given - Test the validator directly with all valid enum values
        ValidBoatTypeValidator validatorImpl = new ValidBoatTypeValidator();
        validatorImpl.initialize(null);

        // When & Then - Test all valid boat types
        for (BoatType boatType : BoatType.values()) {
            boolean isValid = validatorImpl.isValid(boatType.name(), null);
            assertTrue(isValid, "BoatType " + boatType + " should be valid");
        }
    }

    /**
     * Test DTO class to test the ValidBoatType annotation.
     */
    private static class TestBoatTypeDto {
        
        @ValidBoatType
        private final String boatType;
        
        public TestBoatTypeDto(String boatType) {
            this.boatType = boatType;
        }
    }
}