package io.hellorin.boathub.controller;

import io.hellorin.boathub.dto.LoginRequestDto;
import io.hellorin.boathub.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling authentication-related endpoints.
 * Provides login, logout, and user information endpoints.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * Handles user login authentication.
     * Authenticates the user and creates a session if credentials are valid.
     *
     * @param loginRequest the login request containing username and password
     * @param request the HTTP request
     * @return ResponseEntity with user information on success, error on failure
     */
    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequestDto loginRequest, HttpServletRequest request) {
        try {
            var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            // Set authentication in security context and save to session
            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            
            var userDto = new UserDto(loginRequest.getUsername(), true);
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid username or password");
        }
    }

    /**
     * Handles user logout.
     * Invalidates the current session and clears authentication.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @return ResponseEntity indicating successful logout
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return ResponseEntity.ok("Logged out successfully");
    }

    /**
     * Gets the current authenticated user information.
     * Returns user details if authenticated, error if not.
     *
     * @return ResponseEntity with user information or error
     */
    @GetMapping("/user")
    public ResponseEntity<Object> getCurrentUser(@AuthenticationPrincipal UserDetails user) {
        if (user != null && user.getUsername() != null
                && !"anonymousUser".equalsIgnoreCase(user.getUsername())) {
            UserDto userDto = new UserDto(user.getUsername(), true);
            return ResponseEntity.ok(userDto);
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("User not authenticated");
    }
}
