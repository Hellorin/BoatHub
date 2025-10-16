package io.hellorin.boathub.config;

import io.hellorin.boathub.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

/**
 * Security configuration for the application.
 * Configures session-based authentication with form login and CSRF protection.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Configures the security filter chain for HTTP requests.
     * Sets up session-based authentication with form login and CSRF protection.
     *
     * @param http the HttpSecurity configuration
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Configure CSRF token request handler for SPA compatibility
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        // Disable the requirement for the header "X-Requested-With" to allow AJAX requests
        requestHandler.setCsrfRequestAttributeName("_csrf");

        http
            .csrf(csrf -> csrf
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .csrfTokenRequestHandler(requestHandler)
                    .ignoringRequestMatchers("/api/csrf-token", "/api/auth/**") // Allow CSRF token and auth endpoints without CSRF
            )

            // TODO: Session persistence could really help with service restart
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .maximumSessions(1)
                    .maxSessionsPreventsLogin(false)
            )

            .authorizeHttpRequests(authz -> authz
                    // Backend
                    .requestMatchers("/api/csrf-token").permitAll()
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/api/v1/**").authenticated()

                    // Frontend
                    .requestMatchers("/").permitAll()
                    .requestMatchers("/index.html").permitAll()
                    .requestMatchers("/assets/**").permitAll()

                    // Utilities
                    .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
                    .requestMatchers("/actuator/**").permitAll()

                    .anyRequest().authenticated()
            )
            .logout(logout -> logout
                    .logoutUrl("/api/auth/logout")
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
            )
            .userDetailsService(userDetailsService)
            .securityContext(securityContext -> securityContext
                .securityContextRepository(securityContextRepository())
            );

        return http.build();
    }

    /**
     * Configures the password encoder for hashing passwords.
     * Uses BCrypt with strength 12 for secure password hashing.
     *
     * @return the configured PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Configures the authentication manager.
     *
     * @param authConfig the AuthenticationConfiguration
     * @return the configured AuthenticationManager
     * @throws Exception if configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Configures the security context repository for session-based authentication.
     * Uses HTTP session to store security context across requests.
     *
     * @return the configured SecurityContextRepository
     */
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }
}
