package io.hellorin.boathub.dto;

/**
 * Data Transfer Object for user information.
 * Contains user details without sensitive information like password.
 */
public class UserDto {

    private String username;
    private boolean authenticated;

    public UserDto() {
    }

    public UserDto(String username, boolean authenticated) {
        this.username = username;
        this.authenticated = authenticated;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}
