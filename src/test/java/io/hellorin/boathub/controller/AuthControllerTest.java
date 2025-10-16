package io.hellorin.boathub.controller;

import io.hellorin.boathub.dto.LoginRequestDto;
import io.hellorin.boathub.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthController.
 * Tests authentication-related endpoints using mocks for isolation.
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @Mock
    private SecurityContext securityContext;

    private AuthController authController;

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "testpassword";

    @BeforeEach
    void setUp() {
        authController = new AuthController(authenticationManager);
        SecurityContextHolder.clearContext();
    }

    @Test
    void login_WhenValidCredentials_ShouldReturnUserDto() {
        // Given
        var loginRequest = new LoginRequestDto(TEST_USERNAME, TEST_PASSWORD);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(request.getSession()).thenReturn(session);

        // When
        ResponseEntity<Object> result = authController.login(loginRequest, request);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isInstanceOf(UserDto.class);
        
        var userDto = (UserDto) result.getBody();
        assertThat(userDto.getUsername()).isEqualTo(TEST_USERNAME);
        assertThat(userDto.isAuthenticated()).isTrue();

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(request).getSession();
        verify(session).setAttribute(eq("SPRING_SECURITY_CONTEXT"), any(SecurityContext.class));
    }

    @Test
    void login_WhenInvalidCredentials_ShouldReturnUnauthorized() {
        // Given
        var loginRequest = new LoginRequestDto(TEST_USERNAME, "wrongpassword");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When
        ResponseEntity<Object> result = authController.login(loginRequest, request);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(result.getBody()).isEqualTo("Invalid username or password");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(request, never()).getSession();
    }

    @Test
    void login_WhenAuthenticationThrowsException_ShouldReturnUnauthorized() {
        // Given
        var loginRequest = new LoginRequestDto(TEST_USERNAME, TEST_PASSWORD);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new RuntimeException("Authentication service error"));

        // When
        ResponseEntity<Object> result = authController.login(loginRequest, request);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(result.getBody()).isEqualTo("Invalid username or password");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void logout_WhenUserIsAuthenticated_ShouldLogoutSuccessfully() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // When
        ResponseEntity<String> result = authController.logout(request, response);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo("Logged out successfully");

        verify(securityContext).getAuthentication();
    }

    @Test
    void logout_WhenUserIsNotAuthenticated_ShouldReturnOk() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // When
        ResponseEntity<String> result = authController.logout(request, response);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo("Logged out successfully");

        verify(securityContext).getAuthentication();
    }

    @Test
    void getCurrentUser_WhenUserIsAuthenticated_ShouldReturnUserDto() {
        // Given
        when(userDetails.getUsername()).thenReturn(TEST_USERNAME);
        SecurityContextHolder.setContext(securityContext);

        // When
        ResponseEntity<Object> result = authController.getCurrentUser(userDetails);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isInstanceOf(UserDto.class);
        
        var userDto = (UserDto) result.getBody();
        assertThat(userDto.getUsername()).isEqualTo(TEST_USERNAME);
        assertThat(userDto.isAuthenticated()).isTrue();
    }

    @Test
    void getCurrentUser_WhenUserIsNotAuthenticated_ShouldReturnUnauthorized() {

        // When
        ResponseEntity<Object> result = authController.getCurrentUser(userDetails);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(result.getBody()).isEqualTo("User not authenticated");
    }

    @Test
    void getCurrentUser_WhenAuthenticationIsNotAuthenticated_ShouldReturnUnauthorized() {
        // Given
        SecurityContextHolder.setContext(securityContext);

        // When
        ResponseEntity<Object> result = authController.getCurrentUser(userDetails);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(result.getBody()).isEqualTo("User not authenticated");
    }

    @Test
    void getCurrentUser_WhenUserDetailsIsNull_ShouldReturnUnauthorized() {
        // Given
        SecurityContextHolder.setContext(securityContext);

        // When
        ResponseEntity<Object> result = authController.getCurrentUser(null);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(result.getBody()).isEqualTo("User not authenticated");
    }

    @Test
    void getCurrentUser_WhenUsernameIsNull_ShouldReturnUnauthorized() {
        // Given
        when(userDetails.getUsername()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // When
        ResponseEntity<Object> result = authController.getCurrentUser(userDetails);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(result.getBody()).isEqualTo("User not authenticated");
    }

    @Test
    void getCurrentUser_WhenUsernameIsAnonymousUser_ShouldReturnUnauthorized() {
        // Given
        when(userDetails.getUsername()).thenReturn("anonymousUser");
        SecurityContextHolder.setContext(securityContext);

        // When
        ResponseEntity<Object> result = authController.getCurrentUser(userDetails);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(result.getBody()).isEqualTo("User not authenticated");
    }

    @Test
    void getCurrentUser_WhenUsernameIsAnonymousUserCaseInsensitive_ShouldReturnUnauthorized() {
        // Given
        when(userDetails.getUsername()).thenReturn("ANONYMOUSUSER");
        SecurityContextHolder.setContext(securityContext);

        // When
        ResponseEntity<Object> result = authController.getCurrentUser(userDetails);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(result.getBody()).isEqualTo("User not authenticated");
    }
}
