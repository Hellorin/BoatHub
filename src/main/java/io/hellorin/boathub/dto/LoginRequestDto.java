package io.hellorin.boathub.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for login requests.
 * Contains username and password for authentication.
 */
public class LoginRequestDto {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    public LoginRequestDto() {
    }

    public LoginRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
