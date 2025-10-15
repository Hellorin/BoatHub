package io.hellorin.boathub.dto;

import io.hellorin.boathub.domain.BoatType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for BoatEntity.
 * Used for API communication and data validation.
 */
public class BoatEntityDto {
    
    private Long id;
    
    @NotBlank(message = "Boat name is required")
    @Size(max = 100, message = "Boat name must not exceed 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @NotNull(message = "Boat type is required")
    private BoatType boatType;
    
    private LocalDateTime createdDate;
    
    private LocalDateTime updatedDate;
    
    /**
     * Default constructor.
     */
    public BoatEntityDto() {
    }
    
    /**
     * Constructor with all fields.
     * @param id The boat ID
     * @param name The boat name
     * @param description The boat description
     * @param boatType The boat type
     * @param createdDate The creation date
     * @param updatedDate The last update date
     */
    public BoatEntityDto(Long id, String name, String description, BoatType boatType, 
                        LocalDateTime createdDate, LocalDateTime updatedDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.boatType = boatType;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public BoatType getBoatType() {
        return boatType;
    }
    
    public void setBoatType(BoatType boatType) {
        this.boatType = boatType;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }
    
    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
}
