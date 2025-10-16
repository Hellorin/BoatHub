package io.hellorin.boathub.dto;

/**
 * Data Transfer Object for boat creation requests.
 * Contains only the fields that users can provide when creating a new boat.
 */
public class BoatCreationDto extends BaseBoatDto {

    public BoatCreationDto() {
        super();
    }

    public BoatCreationDto(String name, String description, String boatType) {
        super(name, description, boatType);
    }
}
