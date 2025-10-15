package io.hellorin.boathub.dto;

import io.hellorin.boathub.validation.ValidBoatType;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for updating boat type.
 * Contains only the boatType field for PATCH operations.
 */
public class BoatTypeUpdateDto {
    
    @NotBlank(message = "Boat type is required")
    @ValidBoatType
    private String boatType;
    
    /**
     * Default constructor.
     */
    public BoatTypeUpdateDto() {
    }
    
    /**
     * Constructor with boatType field.
     * @param boatType The boat type as string
     */
    public BoatTypeUpdateDto(String boatType) {
        this.boatType = boatType;
    }
    
    public String getBoatType() {
        return boatType;
    }
    
    public void setBoatType(String boatType) {
        this.boatType = boatType;
    }
}
