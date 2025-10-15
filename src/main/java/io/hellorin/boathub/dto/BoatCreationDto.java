package io.hellorin.boathub.dto;

import io.hellorin.boathub.domain.BoatType;

/**
 * Data Transfer Object for boat creation requests.
 * Contains only the fields that users can provide when creating a new boat.
 */
public class BoatCreationDto extends BaseBoatDto {
    
    /**
     * Default constructor.
     */
    public BoatCreationDto() {
        super();
    }
    
    /**
     * Constructor with all fields.
     * @param name The boat name
     * @param description The boat description
     * @param boatType The boat type
     */
    public BoatCreationDto(String name, String description, BoatType boatType) {
        super(name, description, boatType);
    }
}
