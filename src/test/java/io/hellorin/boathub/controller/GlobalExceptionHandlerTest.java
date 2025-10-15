package io.hellorin.boathub.controller;

import io.hellorin.boathub.dto.ErrorResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for GlobalExceptionHandler to verify proper error handling.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void testHandleAllExceptions() {
        // Given
        Exception ex = new Exception("Test exception");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/boats");
        WebRequest webRequest = new ServletWebRequest(request);

        // When
        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleAllExceptions(ex, webRequest);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("An error occurred. Please try again later.", response.getBody().getMessage());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testHandleRuntimeException() {
        // Given
        RuntimeException ex = new RuntimeException("Runtime error");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/boats");
        WebRequest webRequest = new ServletWebRequest(request);

        // When
        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleAllExceptions(ex, webRequest);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("An error occurred. Please try again later.", response.getBody().getMessage());
        assertEquals("Internal Server Error", response.getBody().getError());
    }

    @Test
    void testHandleIllegalArgumentException() {
        // Given
        IllegalArgumentException ex = new IllegalArgumentException("Invalid parameter");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/boats");
        WebRequest webRequest = new ServletWebRequest(request);

        // When
        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleAllExceptions(ex, webRequest);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("An error occurred. Please try again later.", response.getBody().getMessage());
        assertEquals("Internal Server Error", response.getBody().getError());
    }
}
