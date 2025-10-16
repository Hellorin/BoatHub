package io.hellorin.boathub.dto;

import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for updating boat description.
 * Contains only the description field for PATCH operations.
 */
public class BoatDescriptionUpdateDto {
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    public BoatDescriptionUpdateDto() {
    }

    public BoatDescriptionUpdateDto(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
