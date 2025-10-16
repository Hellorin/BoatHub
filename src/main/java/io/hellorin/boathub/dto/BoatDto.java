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

    public BoatDto() {
    }

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
