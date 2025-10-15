package io.hellorin.boathub.dto;

import java.time.LocalDateTime;

/**
 * DTO for error responses to provide consistent error information to clients.
 */
public class ErrorResponseDto {
    
    private String message;
    private final LocalDateTime timestamp;

    public ErrorResponseDto() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponseDto(String message) {
        this();
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}
