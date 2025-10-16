package io.hellorin.boathub.controller;

import io.hellorin.boathub.dto.CsrfTokenResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Controller for providing CSRF tokens to the frontend.
 * This controller provides endpoints for getting CSRF tokens which are required
 * for making authenticated requests to protected endpoints.
 */
@RestController
@RequestMapping("/api")
public class CsrfController {

    /**
     * Gets the CSRF token for the current session.
     * This endpoint can be called by the frontend to retrieve the CSRF token
     * before making authenticated requests.
     * 
     * @param request The HTTP request containing the CSRF token
     * @return CsrfTokenResponse containing the token and header information
     */
    @GetMapping("/csrf-token")
    public CsrfTokenResponse getCsrfToken(HttpServletRequest request) {
        var csrfToken = (CsrfToken) request.getAttribute("_csrf");
        if (csrfToken != null) {
            return new CsrfTokenResponse(
                csrfToken.getToken(),
                csrfToken.getHeaderName(),
                csrfToken.getParameterName()
            );
        }
        
        // This should not happen if CSRF is properly configured
        throw new IllegalStateException("CSRF token not found in request");
    }
}
