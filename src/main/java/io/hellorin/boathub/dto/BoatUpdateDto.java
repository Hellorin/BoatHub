package io.hellorin.boathub.dto;

import io.hellorin.boathub.domain.BoatType;

/**
 * Data Transfer Object for boat update requests.
 * Contains only the fields that users can provide when updating a boat.
 */
public class BoatUpdateDto extends BaseBoatDto {
    
    /**
     * Default constructor.
     */
    public BoatUpdateDto() {
        super();
    }
    
    /**
     * Constructor with all fields.
     * @param name The boat name
     * @param description The boat description
     * @param boatType The boat type
     */
    public BoatUpdateDto(String name, String description, BoatType boatType) {
        super(name, description, boatType);
    }
}
