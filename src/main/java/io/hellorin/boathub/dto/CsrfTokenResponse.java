package io.hellorin.boathub.dto;

/**
 * DTO for CSRF token response.
 * Contains the CSRF token and related metadata for frontend consumption.
 */
public class CsrfTokenResponse {
    private final String token;
    private final String headerName;
    private final String parameterName;
    
    /**
     * Creates a new CSRF token response.
     * 
     * @param token the CSRF token value
     * @param headerName the header name to use when sending the token
     * @param parameterName the parameter name for form submissions
     */
    public CsrfTokenResponse(String token, String headerName, String parameterName) {
        this.token = token;
        this.headerName = headerName;
        this.parameterName = parameterName;
    }
    
    /**
     * Gets the CSRF token value.
     * 
     * @return the token
     */
    public String getToken() { 
        return token; 
    }
    
    /**
     * Gets the header name for sending the token.
     * 
     * @return the header name
     */
    public String getHeaderName() { 
        return headerName; 
    }
    
    /**
     * Gets the parameter name for form submissions.
     * 
     * @return the parameter name
     */
    public String getParameterName() { 
        return parameterName; 
    }
}
