package io.hellorin.boathub.controller;

import io.hellorin.boathub.dto.CsrfTokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.web.csrf.CsrfToken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CsrfController.
 * Tests the CSRF token retrieval functionality in isolation.
 */
@ExtendWith(MockitoExtension.class)
class CsrfControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private CsrfToken csrfToken;

    private final CsrfController csrfController = new CsrfController();

    private static final String TEST_TOKEN = "test-csrf-token-123";
    private static final String TEST_HEADER_NAME = "X-CSRF-TOKEN";
    private static final String TEST_PARAMETER_NAME = "_csrf";

    @BeforeEach
    void setUp() {
        // Setup will be done per test method as needed
    }

    @Test
    void getCsrfToken_WhenTokenExists_ShouldReturnCsrfTokenResponse() {
        // Given
        when(csrfToken.getToken()).thenReturn(TEST_TOKEN);
        when(csrfToken.getHeaderName()).thenReturn(TEST_HEADER_NAME);
        when(csrfToken.getParameterName()).thenReturn(TEST_PARAMETER_NAME);
        when(request.getAttribute("_csrf")).thenReturn(csrfToken);

        // When
        CsrfTokenResponse result = csrfController.getCsrfToken(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo(TEST_TOKEN);
        assertThat(result.getHeaderName()).isEqualTo(TEST_HEADER_NAME);
        assertThat(result.getParameterName()).isEqualTo(TEST_PARAMETER_NAME);
        
        verify(request).getAttribute("_csrf");
    }

    @Test
    void getCsrfToken_WhenTokenIsNull_ShouldThrowIllegalStateException() {
        // Given
        when(request.getAttribute("_csrf")).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> csrfController.getCsrfToken(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("CSRF token not found in request");
        
        verify(request).getAttribute("_csrf");
    }

    @Test
    void getCsrfToken_WhenTokenIsNotCsrfToken_ShouldThrowClassCastException() {
        // Given
        when(request.getAttribute("_csrf")).thenReturn("not-a-csrf-token");

        // When & Then
        assertThatThrownBy(() -> csrfController.getCsrfToken(request))
                .isInstanceOf(ClassCastException.class);
        
        verify(request).getAttribute("_csrf");
    }

    @Test
    void getCsrfToken_WhenTokenHasEmptyValues_ShouldReturnResponseWithEmptyValues() {
        // Given
        when(csrfToken.getToken()).thenReturn("");
        when(csrfToken.getHeaderName()).thenReturn("");
        when(csrfToken.getParameterName()).thenReturn("");
        when(request.getAttribute("_csrf")).thenReturn(csrfToken);

        // When
        CsrfTokenResponse result = csrfController.getCsrfToken(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEmpty();
        assertThat(result.getHeaderName()).isEmpty();
        assertThat(result.getParameterName()).isEmpty();
    }

    @Test
    void getCsrfToken_WhenTokenHasNullValues_ShouldReturnResponseWithNullValues() {
        // Given
        when(csrfToken.getToken()).thenReturn(null);
        when(csrfToken.getHeaderName()).thenReturn(null);
        when(csrfToken.getParameterName()).thenReturn(null);
        when(request.getAttribute("_csrf")).thenReturn(csrfToken);

        // When
        CsrfTokenResponse result = csrfController.getCsrfToken(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isNull();
        assertThat(result.getHeaderName()).isNull();
        assertThat(result.getParameterName()).isNull();
    }
}
