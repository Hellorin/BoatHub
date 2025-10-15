package io.hellorin.boathub.dto;

import io.hellorin.boathub.domain.BoatType;
import io.hellorin.boathub.validation.ValidBoatType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Base DTO containing common boat fields and validation rules.
 * Used as a base for creation and update DTOs.
 */
public abstract class BaseBoatDto {
    
    @NotBlank(message = "Boat name is required")
    @Size(max = 100, message = "Boat name must not exceed 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @NotNull(message = "Boat type is required")
    @ValidBoatType
    private String boatType;
    
    /**
     * Default constructor.
     */
    protected BaseBoatDto() {
    }
    
    /**
     * Constructor with all fields.
     * @param name The boat name
     * @param description The boat description
     * @param boatType The boat type
     */
    protected BaseBoatDto(String name, String description, String boatType) {
        this.name = name;
        this.description = description;
        this.boatType = boatType;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getBoatType() {
        return boatType;
    }
    
    public void setBoatType(String boatType) {
        this.boatType = boatType;
    }
}
