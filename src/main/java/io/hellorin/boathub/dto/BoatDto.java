package io.hellorin.boathub.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for BoatEntity.
 * Used for API communication and data validation.
 */
public class BoatDto extends BaseBoatDto {
    
    private Long id;
    
    private LocalDateTime createdDate;
    
    private LocalDateTime updatedDate;
    
    /**
     * Default constructor.
     */
    public BoatDto() {
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
    public BoatDto(Long id, String name, String description, String boatType,
                   LocalDateTime createdDate, LocalDateTime updatedDate) {
        super(name, description, boatType);
        this.id = id;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
