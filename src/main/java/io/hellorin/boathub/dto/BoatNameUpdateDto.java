package io.hellorin.boathub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for updating boat name.
 * Contains only the name field for PATCH operations.
 */
public class BoatNameUpdateDto {
    
    @NotBlank(message = "Boat name is required")
    @Size(max = 100, message = "Boat name must not exceed 100 characters")
    private String name;

    public BoatNameUpdateDto() {
    }

    public BoatNameUpdateDto(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
