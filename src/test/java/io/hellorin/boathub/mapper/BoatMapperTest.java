package io.hellorin.boathub.mapper;

import io.hellorin.boathub.domain.BoatEntity;
import io.hellorin.boathub.domain.BoatType;
import io.hellorin.boathub.dto.BoatCreationDto;
import io.hellorin.boathub.dto.BoatDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for BoatMapper.
 * Tests the mapping between BoatEntity and BoatEntityDto in both directions.
 */
class BoatMapperTest {

    private BoatMapper boatMapper;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        boatMapper = Mappers.getMapper(BoatMapper.class);
        testDateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
    }

    @Test
    void mapToEntity() {
        // Given
        BoatCreationDto boatDto = new BoatCreationDto();
        boatDto.setName("The Black Pearl");
        boatDto.setDescription("A legendary pirate ship");
        boatDto.setBoatType(BoatType.SAILBOAT.name());

        // When
        BoatEntity result = boatMapper.toEntity(boatDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(boatDto.getName());
        assertThat(result.getDescription()).isEqualTo(boatDto.getDescription());
        assertThat(result.getBoatType().name()).isEqualTo(boatDto.getBoatType());
    }

    @Test
    void mapToDto() {
        // Given
        BoatEntity boatEntity = new BoatEntity();
        boatEntity.setId(2L);
        boatEntity.setName("The Titanic");
        boatEntity.setDescription("A famous ocean liner");
        boatEntity.setBoatType(BoatType.YACHT);
        boatEntity.setCreatedDate(testDateTime);
        boatEntity.setUpdatedDate(testDateTime.plusDays(1));

        // When
        BoatDto result = boatMapper.toDto(boatEntity);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(boatEntity.getId());
        assertThat(result.getName()).isEqualTo(boatEntity.getName());
        assertThat(result.getDescription()).isEqualTo(boatEntity.getDescription());
        assertThat(result.getBoatType()).isEqualTo(boatEntity.getBoatType().name());
        assertThat(result.getCreatedDate()).isEqualTo(boatEntity.getCreatedDate());
        assertThat(result.getUpdatedDate()).isEqualTo(boatEntity.getUpdatedDate());
    }

    @Test
    void mapToEntityWithNullValues() {
        // Given
        BoatCreationDto boatDto = new BoatCreationDto();

        boatDto.setName("Minimal Boat");
        boatDto.setBoatType(BoatType.MOTORBOAT.name());
        // description, createdDate, updatedDate are null

        // When
        BoatEntity result = boatMapper.toEntity(boatDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(boatDto.getName());
        assertThat(result.getDescription()).isNull();
        assertThat(result.getBoatType().name()).isEqualTo(boatDto.getBoatType());
        assertThat(result.getCreatedDate()).isNull();
        assertThat(result.getUpdatedDate()).isNull();
    }

    @Test
    void mapToDtoWithNullValues() {
        // Given
        BoatEntity boatEntity = new BoatEntity();
        boatEntity.setId(4L);
        boatEntity.setName("Another Boat");
        boatEntity.setBoatType(BoatType.FISHING_BOAT);
        // description, createdDate, updatedDate are null

        // When
        BoatDto result = boatMapper.toDto(boatEntity);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(boatEntity.getId());
        assertThat(result.getName()).isEqualTo(boatEntity.getName());
        assertThat(result.getDescription()).isNull();
        assertThat(result.getBoatType()).isEqualTo(boatEntity.getBoatType().name());
        assertThat(result.getCreatedDate()).isNull();
        assertThat(result.getUpdatedDate()).isNull();
    }

    @Test
    void mapToEntityWithAllBoatTypes() {
        // Given
        for (BoatType boatType : BoatType.values()) {
            BoatCreationDto boatDto = new BoatCreationDto();
            boatDto.setName("Test " + boatType.getDisplayName());
            boatDto.setBoatType(boatType.name());

            // When
            BoatEntity result = boatMapper.toEntity(boatDto);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getBoatType()).isEqualTo(boatType);
            assertThat(result.getName()).isEqualTo("Test " + boatType.getDisplayName());
        }
    }

    @Test
    void mapToDtoWithAllBoatTypes() {
        // Given
        for (BoatType boatType : BoatType.values()) {
            BoatEntity boatEntity = new BoatEntity();
            boatEntity.setName("Test " + boatType.getDisplayName());
            boatEntity.setBoatType(boatType);

            // When
            BoatDto result = boatMapper.toDto(boatEntity);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getBoatType()).isEqualTo(boatType.name());
            assertThat(result.getName()).isEqualTo("Test " + boatType.getDisplayName());
        }
    }

    @Test
    void mapToEntityWithEmptyString() {
        // Given
        BoatCreationDto boatDto = new BoatCreationDto();
        boatDto.setName("");
        boatDto.setDescription("");
        boatDto.setBoatType(BoatType.OTHER.name());

        // When
        BoatEntity result = boatMapper.toEntity(boatDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEmpty();
        assertThat(result.getDescription()).isEmpty();
        assertThat(result.getBoatType()).isEqualTo(BoatType.OTHER);
    }

    @Test
    void mapToDtoWithEmptyString() {
        // Given
        BoatEntity boatEntity = new BoatEntity();
        boatEntity.setName("");
        boatEntity.setDescription("");
        boatEntity.setBoatType(BoatType.OTHER);

        // When
        BoatDto result = boatMapper.toDto(boatEntity);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEmpty();
        assertThat(result.getDescription()).isEmpty();
        assertThat(result.getBoatType()).isEqualTo(BoatType.OTHER.name());
    }
}