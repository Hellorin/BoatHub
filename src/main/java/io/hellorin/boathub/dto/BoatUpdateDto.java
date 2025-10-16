package io.hellorin.boathub.dto;

/**
 * Data Transfer Object for boat update requests.
 * Contains all fields that can be updated for an existing boat.
 * All fields are optional to allow partial updates.
 */
public class BoatUpdateDto extends BaseBoatDto {
    /**
     * Default constructor.
     */
    public BoatUpdateDto() {
    }
    
    /**
     * Constructor with all fields.
     * @param name The boat name
     * @param description The boat description
     * @param boatType The boat type
     */
    public BoatUpdateDto(String name, String description, String boatType) {
        super(name, description, boatType);
    }
}
