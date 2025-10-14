package io.hellorin.boathub.mapper;

import io.hellorin.boathub.domain.BoatEntity;
import io.hellorin.boathub.dto.BoatEntityDto;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for converting between BoatEntity and BoatEntityDto.
 * This mapper is configured as a Spring component for dependency injection.
 */
@Mapper(componentModel = "spring")
public interface BoatMapper {

    /**
     * Maps a BoatEntity to a BoatEntityDto.
     *
     * @param boatEntity the source entity
     * @return the mapped DTO
     */
    BoatEntityDto toDto(BoatEntity boatEntity);

    /**
     * Maps a BoatEntityDto to a BoatEntity.
     *
     * @param boatEntityDto the source DTO
     * @return the mapped entity
     */
    BoatEntity toEntity(BoatEntityDto boatEntityDto);
}
