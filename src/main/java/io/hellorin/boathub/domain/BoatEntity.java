package io.hellorin.boathub.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Entity representing a boat in the system.
 * Contains core boat information including identification, description, and audit fields.
 */
@Entity
@Table(name = "boats")
public class BoatEntity {
    
    /**
     * As two boats could have the same name, we use the id to identify the boat. We cannot trust human
     * to find a unique name for their boats
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * By default, the boat name should not be too long. It should be punchy as "The Titanic" or "The Black Pearl"
     */
    @NotBlank(message = "Boat name is required")
    @Size(max = 100, message = "Boat name must not exceed 100 characters")
    @Column(unique = true, nullable = false)
    private String name;
    
    /**
     * 
     */
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    /**
     * To classify them, a boat's type is interesting.
     */
    @NotNull(message = "Boat type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoatType boatType;
    
    /**
     * Keep track of the creation date of the boat.
     */
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    /**
     * Help understand the last time it was updated.
     */
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    
    /**
     * Default constructor for JPA.
     */
    public BoatEntity() {
    }
    
    /**
     * Constructor with required fields.
     * @param name The name of the boat
     * @param boatType The type of the boat
     */
    public BoatEntity(String name, BoatType boatType) {
        this.name = name;
        this.boatType = boatType;
    }
    
   /**
     * This help maintain the created date of the boat with a PrePersist hook
     */
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }
    
    /**
     * This help maintain the updated date of the boat with a PreUpdate hook
     */
    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
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
