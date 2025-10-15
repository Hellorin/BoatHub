package io.hellorin.boathub.validation;

import io.hellorin.boathub.domain.BoatType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

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

    @ParameterizedTest
    @ValueSource(strings = {"SAILBOAT", "MOTORBOAT", "YACHT", "SPEEDBOAT", "FISHING_BOAT", "OTHER"})
    void isValid_WithValidBoatType_ShouldReturnTrue(String boatType) {
        // Given
        TestBoatTypeDto dto = new TestBoatTypeDto(boatType);

        // When
        Set<ConstraintViolation<TestBoatTypeDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Valid boat type should not produce violations").isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"sailboat", "motorboat", "yacht", "speedboat", "fishing_boat", "other"})
    void isValid_WithLowerCaseValidBoatType_ShouldReturnTrue(String boatType) {
        // Given
        TestBoatTypeDto dto = new TestBoatTypeDto(boatType);

        // When
        Set<ConstraintViolation<TestBoatTypeDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Lowercase valid boat type should be accepted").isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Sailboat", "MotorBoat", "Yacht", "SpeedBoat", "Fishing_Boat", "Other"})
    void isValid_WithMixedCaseValidBoatType_ShouldReturnTrue(String boatType) {
        // Given
        TestBoatTypeDto dto = new TestBoatTypeDto(boatType);

        // When
        Set<ConstraintViolation<TestBoatTypeDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Mixed case valid boat type should be accepted").isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {" SAILBOAT ", " MOTORBOAT ", " YACHT ", " SPEEDBOAT ", " FISHING_BOAT ", " OTHER "})
    void isValid_WithValidBoatTypeWithExtraSpaces_ShouldReturnTrue(String boatType) {
        // Given
        TestBoatTypeDto dto = new TestBoatTypeDto(boatType);

        // When
        Set<ConstraintViolation<TestBoatTypeDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Valid boat type with extra spaces should be accepted after trimming").isEmpty();
    }

    @ParameterizedTest
    @CsvSource({
        "SAILBOAT2, 'Boat type with numbers should be invalid'",
        "INVALID_TYPE, 'Invalid boat type should be invalid'",
        "SAILBOAT@, 'Boat type with special characters should be invalid'",
        "SAILBOAT1, 'Boat type with numbers should be invalid'",
        "SAIL, 'Partial boat type should be invalid'"
    })
    void isValid_WithInvalidBoatType_ShouldReturnFalse(String boatType, String expectedMessage) {
        // Given
        TestBoatTypeDto dto = new TestBoatTypeDto(boatType);

        // When
        Set<ConstraintViolation<TestBoatTypeDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as(expectedMessage).isNotEmpty();
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Invalid boat type");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void isValid_WithNullEmptyOrWhitespaceBoatType_ShouldReturnTrue(String boatType) {
        // Given
        TestBoatTypeDto dto = new TestBoatTypeDto(boatType);

        // When
        Set<ConstraintViolation<TestBoatTypeDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).as("Null, empty, or whitespace-only values should be handled by @NotBlank, not @ValidBoatType").isEmpty();
    }

    @ParameterizedTest
    @MethodSource("provideValidBoatTypeValues")
    void isValid_WithValidBoatTypeValues_ShouldReturnTrue(String boatType) {
        // Given - Test the validator directly with all valid enum values
        ValidBoatTypeValidator validatorImpl = new ValidBoatTypeValidator();
        validatorImpl.initialize(null);

        // When
        boolean isValid = validatorImpl.isValid(boatType, null);

        // Then
        assertThat(isValid).as("BoatType %s should be valid", boatType).isTrue();
    }

    /**
     * Provides all valid boat type values for testing.
     * @return Stream of valid boat type strings
     */
    private static Stream<Arguments> provideValidBoatTypeValues() {
        return Stream.of(BoatType.values())
                .map(boatType -> Arguments.of(boatType.name()));
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