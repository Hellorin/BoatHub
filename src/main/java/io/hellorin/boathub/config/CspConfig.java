package io.hellorin.boathub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

/**
 * Content Security Policy configuration for different environments.
 * Provides restrictive CSP headers to prevent XSS and other injection attacks.
 */
@Configuration
public class CspConfig {

    /**
     * Development CSP configuration - allows necessary Vue.js features.
     * More permissive for development convenience.
     *
     * @return StaticHeadersWriter with development CSP
     */
    @Bean
    @Profile("!prod")
    public StaticHeadersWriter developmentCspWriter() {
        return new StaticHeadersWriter("Content-Security-Policy",
            "default-src 'self'; " +
            "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +  // Vue.js requires unsafe-inline/unsafe-eval
            "style-src 'self' 'unsafe-inline'; " +  // CSS requires unsafe-inline
            "img-src 'self' data: blob:; " +  // Allow data URLs and blob URLs for images
            "font-src 'self' data:; " +  // Allow data URLs for fonts
            "connect-src 'self' ws: wss:; " +  // Allow WebSocket connections for dev server
            "frame-ancestors 'none'; " +  // Prevent clickjacking
            "base-uri 'self'; " +  // Restrict base tag
            "form-action 'self'; " +  // Restrict form submissions
            "object-src 'none'; " +  // Block object/embed tags
            "media-src 'none'; " +  // Block media elements
            "manifest-src 'self'; " +  // Allow web app manifest
            "worker-src 'self'; " +  // Allow web workers
            "child-src 'none'; " +  // Block child contexts
            "upgrade-insecure-requests"  // Upgrade HTTP to HTTPS
        );
    }

    /**
     * Production CSP configuration - highly restrictive.
     * Maximum security with minimal permissions.
     *
     * @return StaticHeadersWriter with production CSP
     */
    @Bean
    @Profile("prod")
    public StaticHeadersWriter productionCspWriter() {
        return new StaticHeadersWriter("Content-Security-Policy",
            "default-src 'self'; " +
            "script-src 'self'; " +  // No unsafe-inline or unsafe-eval in production
            "style-src 'self'; " +  // No unsafe-inline in production
            "img-src 'self' data:; " +  // Only self and data URLs
            "font-src 'self'; " +  // Only self
            "connect-src 'self'; " +  // Only self
            "frame-ancestors 'none'; " +  // Prevent clickjacking
            "base-uri 'self'; " +  // Restrict base tag
            "form-action 'self'; " +  // Restrict form submissions
            "object-src 'none'; " +  // Block object/embed tags
            "media-src 'none'; " +  // Block media elements
            "manifest-src 'self'; " +  // Allow web app manifest
            "worker-src 'self'; " +  // Allow web workers
            "child-src 'none'; " +  // Block child contexts
            "upgrade-insecure-requests; " +  // Upgrade HTTP to HTTPS
            "block-all-mixed-content"  // Block mixed content
        );
    }
}
