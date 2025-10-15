package io.hellorin.boathub.controller;

import io.hellorin.boathub.dto.ErrorResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.beans.TypeMismatchException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * Global exception handler to provide consistent error responses and avoid exposing stack traces.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("An error occurred while trying to process the request", ex);

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                "The input is invalid: " + ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles validation errors from @Valid annotations on request bodies.
     *
     * @param ex the MethodArgumentNotValidException that was thrown
     * @return ResponseEntity containing validation error information
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        logger.warn("Validation failed for request body", ex);

        String validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                "'Validation failed':\n " + validationErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles validation errors from constraint violations on method parameters.
     *
     * @param ex the ConstraintViolationException that was thrown
     * @return ResponseEntity containing validation error information
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolationException(ConstraintViolationException ex) {
        logger.warn("Validation failed for method parameters", ex);

        String validationErrors = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                "Validation failed: " + validationErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles HTTP method not supported exceptions.
     *
     * @param ex the HttpRequestMethodNotSupportedException that was thrown
     * @return ResponseEntity containing error information
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        logger.warn("HTTP method not supported: {}", ex.getMessage());

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                "HTTP method '" + ex.getMethod() + "' is not supported for this endpoint"
        );

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }

    /**
     * Handles unsupported media type exceptions.
     *
     * @param ex the HttpMediaTypeNotSupportedException that was thrown
     * @return ResponseEntity containing error information
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        logger.warn("Unsupported media type: {}", ex.getMessage());

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                "Content-Type '" + ex.getContentType() + "' is not supported"
        );

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(errorResponse);
    }

    /**
     * Handles HTTP message not readable exceptions (e.g., invalid JSON).
     *
     * @param ex the HttpMessageNotReadableException that was thrown
     * @return ResponseEntity containing error information
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        logger.warn("HTTP message not readable: {}", ex.getMessage());

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                "Invalid request body format"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles type mismatch exceptions (e.g., invalid path variable types).
     *
     * @param ex the TypeMismatchException that was thrown
     * @return ResponseEntity containing error information
     */
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleTypeMismatchException(TypeMismatchException ex) {
        logger.warn("Type mismatch: {}", ex.getMessage());

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                "Invalid parameter type for '" + ex.getPropertyName() + "': expected " + ex.getRequiredType().getSimpleName()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles all exceptions and returns a generic error response.
     *
     * @param ex the exception that was thrown
     * @param request the web request that caused the exception
     * @return ResponseEntity containing error information
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleAllExceptions(Exception ex, WebRequest request) {
        logger.error("An error occurred: {}", ex.getMessage(), ex);
        
        ErrorResponseDto errorResponse = new ErrorResponseDto(
            "An error occurred. Please try again later."
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
