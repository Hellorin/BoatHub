package io.hellorin.boathub.mapper;

import io.hellorin.boathub.domain.BoatEntity;
import io.hellorin.boathub.dto.BoatCreationDto;
import io.hellorin.boathub.dto.BoatDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for converting between BoatEntity and BoatEntityDto.
 * This mapper is configured as a Spring component for dependency injection.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BoatMapper {

    /**
     * Maps a BoatEntity to a BoatEntityDto.
     *
     * @param boatEntity the source entity
     * @return the mapped DTO
     */
    BoatDto toDto(BoatEntity boatEntity);

    /**
     * Maps a BoatCreationDto to a BoatEntity.
     *
     * @param boatCreationDto the source base DTO
     * @return the mapped entity
     */
    BoatEntity toEntity(BoatCreationDto boatCreationDto);
}
