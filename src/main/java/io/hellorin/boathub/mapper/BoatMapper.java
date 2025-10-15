package io.hellorin.boathub.mapper;

import io.hellorin.boathub.domain.BoatEntity;
import io.hellorin.boathub.dto.BaseBoatDto;
import io.hellorin.boathub.dto.BoatDto;
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
    BoatDto toDto(BoatEntity boatEntity);

    /**
     * Maps a BoatEntityDto to a BoatEntity.
     *
     * @param boatDto the source DTO
     * @return the mapped entity
     */
    BoatEntity toEntity(BoatDto boatDto);

    /**
     * Maps a BaseBoatDto to a BoatEntity.
     * This method will be used by both BoatCreationDto and BoatUpdateDto.
     *
     * @param baseBoatDto the source base DTO
     * @return the mapped entity
     */
    BoatEntity toEntity(BaseBoatDto baseBoatDto);
}
