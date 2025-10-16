package io.hellorin.boathub.dto;

/**
 * Data Transfer Object for boat update requests.
 * Contains all fields that can be updated for an existing boat.
 * All fields are optional to allow partial updates.
 */
public class BoatUpdateDto extends BaseBoatDto {

    public BoatUpdateDto() {
    }

    public BoatUpdateDto(String name, String description, String boatType) {
        super(name, description, boatType);
    }
}
