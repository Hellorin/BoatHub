package io.hellorin.boathub.service;

import io.hellorin.boathub.domain.BoatEntity;
import io.hellorin.boathub.domain.BoatType;
import io.hellorin.boathub.dto.BoatDto;
import io.hellorin.boathub.dto.BoatCreationDto;
import io.hellorin.boathub.dto.BoatNameUpdateDto;
import io.hellorin.boathub.dto.BoatDescriptionUpdateDto;
import io.hellorin.boathub.dto.BoatTypeUpdateDto;
import io.hellorin.boathub.mapper.BoatMapper;
import io.hellorin.boathub.repository.BoatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BoatService class.
 * Tests all service methods with various scenarios including success and edge cases.
 */
@ExtendWith(MockitoExtension.class)
class BoatServiceTest {

    @Mock
    private BoatRepository boatRepository;

    @Mock
    private BoatMapper boatMapper;

    private BoatService boatService;

    private BoatEntity testBoatEntity;
    private BoatDto testBoatDto;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        // Initialize service with mocked dependencies
        boatService = new BoatService(boatMapper, boatRepository);
        
        testDateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        
        testBoatEntity = new BoatEntity();
        testBoatEntity.setId(1L);
        testBoatEntity.setName("Test Boat");
        testBoatEntity.setDescription("A test boat for unit testing");
        testBoatEntity.setBoatType(BoatType.SAILBOAT);
        testBoatEntity.setCreatedDate(testDateTime);
        testBoatEntity.setUpdatedDate(testDateTime);

        testBoatDto = new BoatDto();
        testBoatDto.setId(1L);
        testBoatDto.setName("Test Boat");
        testBoatDto.setDescription("A test boat for unit testing");
        testBoatDto.setBoatType(BoatType.SAILBOAT);
        testBoatDto.setCreatedDate(testDateTime);
        testBoatDto.setUpdatedDate(testDateTime);
    }

    @Test
    void getAllBoats_WhenBoatsExist_ShouldReturnListOfBoatsInPage() {
        // Given
        BoatEntity boatEntity2 = new BoatEntity();
        boatEntity2.setId(2L);
        boatEntity2.setName("Another Boat");
        boatEntity2.setDescription("Another test boat");
        boatEntity2.setBoatType(BoatType.MOTORBOAT);
        boatEntity2.setCreatedDate(testDateTime);
        boatEntity2.setUpdatedDate(testDateTime);

        BoatDto boatDto2 = new BoatDto();
        boatDto2.setId(2L);
        boatDto2.setName("Another Boat");
        boatDto2.setDescription("Another test boat");
        boatDto2.setBoatType(BoatType.MOTORBOAT);
        boatDto2.setCreatedDate(testDateTime);
        boatDto2.setUpdatedDate(testDateTime);

        List<BoatEntity> boatEntities = Arrays.asList(testBoatEntity, boatEntity2);
        List<BoatDto> expectedDtos = Arrays.asList(testBoatDto, boatDto2);
        Page<BoatEntity> boatEntityPage = new PageImpl<>(boatEntities);
        Pageable pageable = PageRequest.of(0, 10);

        when(boatRepository.findAll(pageable)).thenReturn(boatEntityPage);
        when(boatMapper.toDto(testBoatEntity)).thenReturn(testBoatDto);
        when(boatMapper.toDto(boatEntity2)).thenReturn(boatDto2);

        // When
        Page<BoatDto> result = boatService.getAllBoatsInPage(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).isEqualTo(expectedDtos);
        
        verify(boatRepository).findAll(pageable);
        verify(boatMapper).toDto(testBoatEntity);
        verify(boatMapper).toDto(boatEntity2);
    }

    @Test
    void getAllBoats_WhenNoBoatsInPageExist_ShouldReturnEmptyList() {
        // Given
        Page<BoatEntity> emptyPage = new PageImpl<>(Collections.emptyList());
        Pageable pageable = PageRequest.of(0, 10);
        when(boatRepository.findAll(pageable)).thenReturn(emptyPage);

        // When
        Page<BoatDto> result = boatService.getAllBoatsInPage(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        
        verify(boatRepository).findAll(pageable);
        verify(boatMapper, never()).toDto(any());
    }

    @Test
    void getAllBoats_InPage_WhenRepositoryThrowsException_ShouldPropagateException() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        when(boatRepository.findAll(pageable)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> boatService.getAllBoatsInPage(pageable));
        
        verify(boatRepository).findAll(pageable);
        verify(boatMapper, never()).toDto(any());
    }

    @Test
    void getBoatById_WhenBoatExists_ShouldReturnBoatDto() {
        // Given
        Long boatId = 1L;
        when(boatRepository.findById(boatId)).thenReturn(Optional.of(testBoatEntity));
        when(boatMapper.toDto(testBoatEntity)).thenReturn(testBoatDto);

        // When
        Optional<BoatDto> result = boatService.getBoatById(boatId);

        // Then
        assertThat(result).isPresent().contains(testBoatDto);
        
        verify(boatRepository).findById(boatId);
        verify(boatMapper).toDto(testBoatEntity);
    }

    @Test
    void getBoatById_WhenBoatDoesNotExist_ShouldReturnEmptyOptional() {
        // Given
        Long boatId = 999L;
        when(boatRepository.findById(boatId)).thenReturn(Optional.empty());

        // When
        Optional<BoatDto> result = boatService.getBoatById(boatId);

        // Then
        assertThat(result).isEmpty();
        
        verify(boatRepository).findById(boatId);
        verify(boatMapper, never()).toDto(any());
    }

    @Test
    void getBoatById_WhenIdIsNull_ShouldReturnEmptyOptional() {
        // Given
        when(boatRepository.findById(null)).thenReturn(Optional.empty());

        // When
        Optional<BoatDto> result = boatService.getBoatById(null);

        // Then
        assertThat(result).isEmpty();
        
        verify(boatRepository).findById(null);
        verify(boatMapper, never()).toDto(any());
    }

    @Test
    void getBoatById_WhenRepositoryThrowsException_ShouldPropagateException() {
        // Given
        Long boatId = 1L;
        when(boatRepository.findById(boatId)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> boatService.getBoatById(boatId));
        
        verify(boatRepository).findById(boatId);
        verify(boatMapper, never()).toDto(any());
    }

    @Test
    void getBoatById_WhenMapperThrowsException_ShouldPropagateException() {
        // Given
        Long boatId = 1L;
        when(boatRepository.findById(boatId)).thenReturn(Optional.of(testBoatEntity));
        when(boatMapper.toDto(testBoatEntity)).thenThrow(new RuntimeException("Mapping error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> boatService.getBoatById(boatId));
        
        verify(boatRepository).findById(boatId);
        verify(boatMapper).toDto(testBoatEntity);
    }

    @Test
    void getAllBoats_InPage_WhenMapperReturnsNull_ShouldHandleGracefully() {
        // Given
        Page<BoatEntity> boatEntityPage = new PageImpl<>(Arrays.asList(testBoatEntity));
        Pageable pageable = PageRequest.of(0, 10);
        when(boatRepository.findAll(pageable)).thenReturn(boatEntityPage);
        when(boatMapper.toDto(testBoatEntity)).thenReturn(null);

        // When
        Page<BoatDto> result = boatService.getAllBoatsInPage(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        
        verify(boatRepository).findAll(pageable);
        verify(boatMapper).toDto(testBoatEntity);
    }

    @Test
    void getBoatById_WithDifferentBoatTypes_ShouldMapCorrectly() {
        // Given
        BoatEntity fishingBoatEntity = new BoatEntity();
        fishingBoatEntity.setId(2L);
        fishingBoatEntity.setName("Fishing Vessel");
        fishingBoatEntity.setDescription("A boat for fishing");
        fishingBoatEntity.setBoatType(BoatType.FISHING_BOAT);
        fishingBoatEntity.setCreatedDate(testDateTime);
        fishingBoatEntity.setUpdatedDate(testDateTime);

        BoatDto fishingBoatDto = new BoatDto();
        fishingBoatDto.setId(2L);
        fishingBoatDto.setName("Fishing Vessel");
        fishingBoatDto.setDescription("A boat for fishing");
        fishingBoatDto.setBoatType(BoatType.FISHING_BOAT);
        fishingBoatDto.setCreatedDate(testDateTime);
        fishingBoatDto.setUpdatedDate(testDateTime);

        Long boatId = 2L;
        when(boatRepository.findById(boatId)).thenReturn(Optional.of(fishingBoatEntity));
        when(boatMapper.toDto(fishingBoatEntity)).thenReturn(fishingBoatDto);

        // When
        Optional<BoatDto> result = boatService.getBoatById(boatId);

        // Then
        assertThat(result).isPresent().contains(fishingBoatDto);
        assertThat(result.get().getBoatType()).isEqualTo(BoatType.FISHING_BOAT);
        
        verify(boatRepository).findById(boatId);
        verify(boatMapper).toDto(fishingBoatEntity);
    }

    @Test
    void getAllBoats_WithMultipleBoatTypes_ShouldReturnAllBoatsInPage() {
        // Given
        BoatEntity yachtEntity = new BoatEntity();
        yachtEntity.setId(3L);
        yachtEntity.setName("Luxury Yacht");
        yachtEntity.setDescription("A luxurious yacht");
        yachtEntity.setBoatType(BoatType.YACHT);
        yachtEntity.setCreatedDate(testDateTime);
        yachtEntity.setUpdatedDate(testDateTime);

        BoatDto yachtDto = new BoatDto();
        yachtDto.setId(3L);
        yachtDto.setName("Luxury Yacht");
        yachtDto.setDescription("A luxurious yacht");
        yachtDto.setBoatType(BoatType.YACHT);
        yachtDto.setCreatedDate(testDateTime);
        yachtDto.setUpdatedDate(testDateTime);

        List<BoatEntity> boatEntities = Arrays.asList(testBoatEntity, yachtEntity);
        List<BoatDto> expectedDtos = Arrays.asList(testBoatDto, yachtDto);
        Page<BoatEntity> boatEntityPage = new PageImpl<>(boatEntities);
        Pageable pageable = PageRequest.of(0, 10);

        when(boatRepository.findAll(pageable)).thenReturn(boatEntityPage);
        when(boatMapper.toDto(testBoatEntity)).thenReturn(testBoatDto);
        when(boatMapper.toDto(yachtEntity)).thenReturn(yachtDto);

        // When
        Page<BoatDto> result = boatService.getAllBoatsInPage(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).isEqualTo(expectedDtos);
        
        // Verify all boat types are present
        List<BoatType> boatTypes = result.getContent().stream()
                .map(BoatDto::getBoatType)
                .toList();
        assertThat(boatTypes).contains(BoatType.SAILBOAT).contains(BoatType.YACHT);
        
        verify(boatRepository).findAll(pageable);
        verify(boatMapper).toDto(testBoatEntity);
        verify(boatMapper).toDto(yachtEntity);
    }

    @Test
    void updateBoatName_WhenBoatExists_ShouldUpdateNameAndReturnUpdatedBoat() {
        // Given
        Long boatId = 1L;
        String newName = "Updated Boat Name";
        BoatNameUpdateDto nameUpdateDto = new BoatNameUpdateDto(newName);
        
        BoatEntity updatedEntity = new BoatEntity();
        updatedEntity.setId(1L);
        updatedEntity.setName(newName);
        updatedEntity.setDescription("A test boat for unit testing");
        updatedEntity.setBoatType(BoatType.SAILBOAT);
        updatedEntity.setCreatedDate(testDateTime);
        updatedEntity.setUpdatedDate(testDateTime.plusHours(1));
        
        BoatDto updatedDto = new BoatDto();
        updatedDto.setId(1L);
        updatedDto.setName(newName);
        updatedDto.setDescription("A test boat for unit testing");
        updatedDto.setBoatType(BoatType.SAILBOAT);
        updatedDto.setCreatedDate(testDateTime);
        updatedDto.setUpdatedDate(testDateTime.plusHours(1));

        when(boatRepository.findById(boatId)).thenReturn(Optional.of(testBoatEntity));
        when(boatRepository.save(any(BoatEntity.class))).thenReturn(updatedEntity);
        when(boatMapper.toDto(updatedEntity)).thenReturn(updatedDto);

        // When
        Optional<BoatDto> result = boatService.updateBoatName(boatId, nameUpdateDto);

        // Then
        assertThat(result).isPresent().contains(updatedDto);
        assertThat(result.get().getName()).isEqualTo(newName);
        
        verify(boatRepository).findById(boatId);
        verify(boatRepository).save(any(BoatEntity.class));
        verify(boatMapper).toDto(updatedEntity);
    }

    @Test
    void updateBoatName_WhenBoatDoesNotExist_ShouldReturnEmptyOptional() {
        // Given
        Long boatId = 999L;
        BoatNameUpdateDto nameUpdateDto = new BoatNameUpdateDto("New Name");
        when(boatRepository.findById(boatId)).thenReturn(Optional.empty());

        // When
        Optional<BoatDto> result = boatService.updateBoatName(boatId, nameUpdateDto);

        // Then
        assertThat(result).isEmpty();
        
        verify(boatRepository).findById(boatId);
        verify(boatRepository, never()).save(any());
        verify(boatMapper, never()).toDto(any());
    }

    @Test
    void updateBoatDescription_WhenBoatExists_ShouldUpdateDescriptionAndReturnUpdatedBoat() {
        // Given
        Long boatId = 1L;
        String newDescription = "Updated boat description";
        BoatDescriptionUpdateDto descriptionUpdateDto = new BoatDescriptionUpdateDto(newDescription);
        
        BoatEntity updatedEntity = new BoatEntity();
        updatedEntity.setId(1L);
        updatedEntity.setName("Test Boat");
        updatedEntity.setDescription(newDescription);
        updatedEntity.setBoatType(BoatType.SAILBOAT);
        updatedEntity.setCreatedDate(testDateTime);
        updatedEntity.setUpdatedDate(testDateTime.plusHours(1));
        
        BoatDto updatedDto = new BoatDto();
        updatedDto.setId(1L);
        updatedDto.setName("Test Boat");
        updatedDto.setDescription(newDescription);
        updatedDto.setBoatType(BoatType.SAILBOAT);
        updatedDto.setCreatedDate(testDateTime);
        updatedDto.setUpdatedDate(testDateTime.plusHours(1));

        when(boatRepository.findById(boatId)).thenReturn(Optional.of(testBoatEntity));
        when(boatRepository.save(any(BoatEntity.class))).thenReturn(updatedEntity);
        when(boatMapper.toDto(updatedEntity)).thenReturn(updatedDto);

        // When
        Optional<BoatDto> result = boatService.updateBoatDescription(boatId, descriptionUpdateDto);

        // Then
        assertThat(result).isPresent().contains(updatedDto);
        assertThat(result.get().getDescription()).isEqualTo(newDescription);
        
        verify(boatRepository).findById(boatId);
        verify(boatRepository).save(any(BoatEntity.class));
        verify(boatMapper).toDto(updatedEntity);
    }

    @Test
    void updateBoatDescription_WhenBoatDoesNotExist_ShouldReturnEmptyOptional() {
        // Given
        Long boatId = 999L;
        BoatDescriptionUpdateDto descriptionUpdateDto = new BoatDescriptionUpdateDto("New Description");
        when(boatRepository.findById(boatId)).thenReturn(Optional.empty());

        // When
        Optional<BoatDto> result = boatService.updateBoatDescription(boatId, descriptionUpdateDto);

        // Then
        assertThat(result).isEmpty();
        
        verify(boatRepository).findById(boatId);
        verify(boatRepository, never()).save(any());
        verify(boatMapper, never()).toDto(any());
    }

    @Test
    void updateBoatType_WhenBoatExists_ShouldUpdateTypeAndReturnUpdatedBoat() {
        // Given
        Long boatId = 1L;
        BoatType newType = BoatType.MOTORBOAT;
        BoatTypeUpdateDto typeUpdateDto = new BoatTypeUpdateDto("MOTORBOAT");
        
        BoatEntity updatedEntity = new BoatEntity();
        updatedEntity.setId(1L);
        updatedEntity.setName("Test Boat");
        updatedEntity.setDescription("A test boat for unit testing");
        updatedEntity.setBoatType(newType);
        updatedEntity.setCreatedDate(testDateTime);
        updatedEntity.setUpdatedDate(testDateTime.plusHours(1));
        
        BoatDto updatedDto = new BoatDto();
        updatedDto.setId(1L);
        updatedDto.setName("Test Boat");
        updatedDto.setDescription("A test boat for unit testing");
        updatedDto.setBoatType(newType);
        updatedDto.setCreatedDate(testDateTime);
        updatedDto.setUpdatedDate(testDateTime.plusHours(1));

        when(boatRepository.findById(boatId)).thenReturn(Optional.of(testBoatEntity));
        when(boatRepository.save(any(BoatEntity.class))).thenReturn(updatedEntity);
        when(boatMapper.toDto(updatedEntity)).thenReturn(updatedDto);

        // When
        Optional<BoatDto> result = boatService.updateBoatType(boatId, typeUpdateDto);

        // Then
        assertThat(result).contains(updatedDto);
        assertThat(result.get().getBoatType()).isEqualTo(newType);
        
        verify(boatRepository).findById(boatId);
        verify(boatRepository).save(any(BoatEntity.class));
        verify(boatMapper).toDto(updatedEntity);
    }

    @Test
    void updateBoatType_WhenBoatDoesNotExist_ShouldReturnEmptyOptional() {
        // Given
        Long boatId = 999L;
        BoatTypeUpdateDto typeUpdateDto = new BoatTypeUpdateDto("YACHT");
        when(boatRepository.findById(boatId)).thenReturn(Optional.empty());

        // When
        Optional<BoatDto> result = boatService.updateBoatType(boatId, typeUpdateDto);

        // Then
        assertThat(result).isEmpty();
        
        verify(boatRepository).findById(boatId);
        verify(boatRepository, never()).save(any());
        verify(boatMapper, never()).toDto(any());
    }

    @Test
    void updateBoatName_WhenRepositoryThrowsException_ShouldPropagateException() {
        // Given
        Long boatId = 1L;
        BoatNameUpdateDto nameUpdateDto = new BoatNameUpdateDto("New Name");
        when(boatRepository.findById(boatId)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> boatService.updateBoatName(boatId, nameUpdateDto));
        
        verify(boatRepository).findById(boatId);
        verify(boatRepository, never()).save(any());
        verify(boatMapper, never()).toDto(any());
    }

    @Test
    void updateBoatDescription_WhenRepositoryThrowsException_ShouldPropagateException() {
        // Given
        Long boatId = 1L;
        BoatDescriptionUpdateDto descriptionUpdateDto = new BoatDescriptionUpdateDto("New Description");
        when(boatRepository.findById(boatId)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> boatService.updateBoatDescription(boatId, descriptionUpdateDto));
        
        verify(boatRepository).findById(boatId);
        verify(boatRepository, never()).save(any());
        verify(boatMapper, never()).toDto(any());
    }

    @Test
    void updateBoatType_WhenRepositoryThrowsException_ShouldPropagateException() {
        // Given
        Long boatId = 1L;
        BoatTypeUpdateDto typeUpdateDto = new BoatTypeUpdateDto("FISHING_BOAT");
        when(boatRepository.findById(boatId)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> boatService.updateBoatType(boatId, typeUpdateDto));
        
        verify(boatRepository).findById(boatId);
        verify(boatRepository, never()).save(any());
        verify(boatMapper, never()).toDto(any());
    }

    @Test
    void createBoat_WhenValidData_ShouldCreateAndReturnBoatDto() {
        // Given
        BoatCreationDto creationDto = new BoatCreationDto("New Boat", "A new test boat", "SAILBOAT");
        
        BoatEntity entityToSave = new BoatEntity();
        entityToSave.setName("New Boat");
        entityToSave.setDescription("A new test boat");
        entityToSave.setBoatType(BoatType.SAILBOAT);
        entityToSave.setCreatedDate(testDateTime);
        entityToSave.setUpdatedDate(testDateTime);
        
        BoatEntity savedEntity = new BoatEntity();
        savedEntity.setId(1L);
        savedEntity.setName("New Boat");
        savedEntity.setDescription("A new test boat");
        savedEntity.setBoatType(BoatType.SAILBOAT);
        savedEntity.setCreatedDate(testDateTime);
        savedEntity.setUpdatedDate(testDateTime);
        
        BoatDto expectedDto = new BoatDto();
        expectedDto.setId(1L);
        expectedDto.setName("New Boat");
        expectedDto.setDescription("A new test boat");
        expectedDto.setBoatType(BoatType.SAILBOAT);
        expectedDto.setCreatedDate(testDateTime);
        expectedDto.setUpdatedDate(testDateTime);

        when(boatMapper.toEntity(creationDto)).thenReturn(entityToSave);
        when(boatRepository.save(any(BoatEntity.class))).thenReturn(savedEntity);
        when(boatMapper.toDto(savedEntity)).thenReturn(expectedDto);

        // When
        BoatDto result = boatService.createBoat(creationDto);

        // Then
        assertThat(result).isNotNull().isEqualTo(expectedDto);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("New Boat");
        assertThat(result.getDescription()).isEqualTo("A new test boat");
        assertThat(result.getBoatType()).isEqualTo(BoatType.SAILBOAT);
        
        verify(boatMapper).toEntity(creationDto);
        verify(boatRepository).save(any(BoatEntity.class));
        verify(boatMapper).toDto(savedEntity);
    }

    @Test
    void createBoat_WithDifferentBoatTypes_ShouldCreateCorrectly() {
        // Given
        BoatCreationDto creationDto = new BoatCreationDto("Motor Boat", "A motor boat", "MOTORBOAT");
        
        BoatEntity entityToSave = new BoatEntity();
        entityToSave.setName("Motor Boat");
        entityToSave.setDescription("A motor boat");
        entityToSave.setBoatType(BoatType.MOTORBOAT);
        entityToSave.setCreatedDate(testDateTime);
        entityToSave.setUpdatedDate(testDateTime);
        
        BoatEntity savedEntity = new BoatEntity();
        savedEntity.setId(2L);
        savedEntity.setName("Motor Boat");
        savedEntity.setDescription("A motor boat");
        savedEntity.setBoatType(BoatType.MOTORBOAT);
        savedEntity.setCreatedDate(testDateTime);
        savedEntity.setUpdatedDate(testDateTime);
        
        BoatDto expectedDto = new BoatDto();
        expectedDto.setId(2L);
        expectedDto.setName("Motor Boat");
        expectedDto.setDescription("A motor boat");
        expectedDto.setBoatType(BoatType.MOTORBOAT);
        expectedDto.setCreatedDate(testDateTime);
        expectedDto.setUpdatedDate(testDateTime);

        when(boatMapper.toEntity(creationDto)).thenReturn(entityToSave);
        when(boatRepository.save(any(BoatEntity.class))).thenReturn(savedEntity);
        when(boatMapper.toDto(savedEntity)).thenReturn(expectedDto);

        // When
        BoatDto result = boatService.createBoat(creationDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBoatType()).isEqualTo(BoatType.MOTORBOAT);
        assertThat(result.getName()).isEqualTo("Motor Boat");
        
        verify(boatMapper).toEntity(creationDto);
        verify(boatRepository).save(any(BoatEntity.class));
        verify(boatMapper).toDto(savedEntity);
    }

    @Test
    void createBoat_WhenMapperThrowsException_ShouldPropagateException() {
        // Given
        BoatCreationDto creationDto = new BoatCreationDto("Test Boat", "Test Description", "YACHT");
        when(boatMapper.toEntity(creationDto)).thenThrow(new RuntimeException("Mapping error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> boatService.createBoat(creationDto));
        
        verify(boatMapper).toEntity(creationDto);
        verify(boatRepository, never()).save(any());
        verify(boatMapper, never()).toDto(any());
    }

    @Test
    void createBoat_WhenRepositoryThrowsException_ShouldPropagateException() {
        // Given
        BoatCreationDto creationDto = new BoatCreationDto("Test Boat", "Test Description", "FISHING_BOAT");
        BoatEntity entityToSave = new BoatEntity();
        entityToSave.setName("Test Boat");
        entityToSave.setDescription("Test Description");
        entityToSave.setBoatType(BoatType.FISHING_BOAT);
        
        when(boatMapper.toEntity(creationDto)).thenReturn(entityToSave);
        when(boatRepository.save(any(BoatEntity.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> boatService.createBoat(creationDto));
        
        verify(boatMapper).toEntity(creationDto);
        verify(boatRepository).save(any(BoatEntity.class));
        verify(boatMapper, never()).toDto(any());
    }

    @Test
    void createBoat_WhenMapperToDtoThrowsException_ShouldPropagateException() {
        // Given
        BoatCreationDto creationDto = new BoatCreationDto("Test Boat", "Test Description", "YACHT");
        BoatEntity entityToSave = new BoatEntity();
        entityToSave.setName("Test Boat");
        entityToSave.setDescription("Test Description");
        entityToSave.setBoatType(BoatType.YACHT);
        
        BoatEntity savedEntity = new BoatEntity();
        savedEntity.setId(1L);
        savedEntity.setName("Test Boat");
        savedEntity.setDescription("Test Description");
        savedEntity.setBoatType(BoatType.YACHT);
        
        when(boatMapper.toEntity(creationDto)).thenReturn(entityToSave);
        when(boatRepository.save(any(BoatEntity.class))).thenReturn(savedEntity);
        when(boatMapper.toDto(savedEntity)).thenThrow(new RuntimeException("DTO mapping error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> boatService.createBoat(creationDto));
        
        verify(boatMapper).toEntity(creationDto);
        verify(boatRepository).save(any(BoatEntity.class));
        verify(boatMapper).toDto(savedEntity);
    }

    @Test
    void deleteBoat_WhenBoatExists_ShouldDeleteAndReturnTrue() {
        // Given
        Long boatId = 1L;
        when(boatRepository.existsById(boatId)).thenReturn(true);

        // When
        boolean result = boatService.deleteBoat(boatId);

        // Then
        assertThat(result).isTrue();
        
        verify(boatRepository).existsById(boatId);
        verify(boatRepository).deleteById(boatId);
    }

    @Test
    void deleteBoat_WhenBoatDoesNotExist_ShouldReturnFalse() {
        // Given
        Long boatId = 999L;
        when(boatRepository.existsById(boatId)).thenReturn(false);

        // When
        boolean result = boatService.deleteBoat(boatId);

        // Then
        assertThat(result).isFalse();
        
        verify(boatRepository).existsById(boatId);
        verify(boatRepository, never()).deleteById(any());
    }

    @Test
    void deleteBoat_WhenIdIsNull_ShouldReturnFalse() {
        // Given
        when(boatRepository.existsById(null)).thenReturn(false);

        // When
        boolean result = boatService.deleteBoat(null);

        // Then
        assertThat(result).isFalse();
        
        verify(boatRepository).existsById(null);
        verify(boatRepository, never()).deleteById(any());
    }

    @Test
    void deleteBoat_WhenExistsByIdThrowsException_ShouldPropagateException() {
        // Given
        Long boatId = 1L;
        when(boatRepository.existsById(boatId)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> boatService.deleteBoat(boatId));
        
        verify(boatRepository).existsById(boatId);
        verify(boatRepository, never()).deleteById(any());
    }

    @Test
    void deleteBoat_WhenDeleteByIdThrowsException_ShouldPropagateException() {
        // Given
        Long boatId = 1L;
        when(boatRepository.existsById(boatId)).thenReturn(true);
        doThrow(new RuntimeException("Delete error")).when(boatRepository).deleteById(boatId);

        // When & Then
        assertThrows(RuntimeException.class, () -> boatService.deleteBoat(boatId));
        
        verify(boatRepository).existsById(boatId);
        verify(boatRepository).deleteById(boatId);
    }

    @Test
    void updateBoatType_WithLowerCaseBoatType_ShouldConvertToUpperCase() {
        // Given
        Long boatId = 1L;
        BoatTypeUpdateDto typeUpdateDto = new BoatTypeUpdateDto("motorboat");
        
        BoatEntity updatedEntity = new BoatEntity();
        updatedEntity.setId(1L);
        updatedEntity.setName("Test Boat");
        updatedEntity.setDescription("A test boat for unit testing");
        updatedEntity.setBoatType(BoatType.MOTORBOAT);
        updatedEntity.setCreatedDate(testDateTime);
        updatedEntity.setUpdatedDate(testDateTime.plusHours(1));
        
        BoatDto updatedDto = new BoatDto();
        updatedDto.setId(1L);
        updatedDto.setName("Test Boat");
        updatedDto.setDescription("A test boat for unit testing");
        updatedDto.setBoatType(BoatType.MOTORBOAT);
        updatedDto.setCreatedDate(testDateTime);
        updatedDto.setUpdatedDate(testDateTime.plusHours(1));

        when(boatRepository.findById(boatId)).thenReturn(Optional.of(testBoatEntity));
        when(boatRepository.save(any(BoatEntity.class))).thenReturn(updatedEntity);
        when(boatMapper.toDto(updatedEntity)).thenReturn(updatedDto);

        // When
        Optional<BoatDto> result = boatService.updateBoatType(boatId, typeUpdateDto);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getBoatType()).isEqualTo(BoatType.MOTORBOAT);
        
        verify(boatRepository).findById(boatId);
        verify(boatRepository).save(any(BoatEntity.class));
        verify(boatMapper).toDto(updatedEntity);
    }

    @Test
    void updateBoatType_WithMixedCaseBoatType_ShouldConvertToUpperCase() {
        // Given
        Long boatId = 1L;
        BoatTypeUpdateDto typeUpdateDto = new BoatTypeUpdateDto("Fishing_Boat");
        
        BoatEntity updatedEntity = new BoatEntity();
        updatedEntity.setId(1L);
        updatedEntity.setName("Test Boat");
        updatedEntity.setDescription("A test boat for unit testing");
        updatedEntity.setBoatType(BoatType.FISHING_BOAT);
        updatedEntity.setCreatedDate(testDateTime);
        updatedEntity.setUpdatedDate(testDateTime.plusHours(1));
        
        BoatDto updatedDto = new BoatDto();
        updatedDto.setId(1L);
        updatedDto.setName("Test Boat");
        updatedDto.setDescription("A test boat for unit testing");
        updatedDto.setBoatType(BoatType.FISHING_BOAT);
        updatedDto.setCreatedDate(testDateTime);
        updatedDto.setUpdatedDate(testDateTime.plusHours(1));

        when(boatRepository.findById(boatId)).thenReturn(Optional.of(testBoatEntity));
        when(boatRepository.save(any(BoatEntity.class))).thenReturn(updatedEntity);
        when(boatMapper.toDto(updatedEntity)).thenReturn(updatedDto);

        // When
        Optional<BoatDto> result = boatService.updateBoatType(boatId, typeUpdateDto);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getBoatType()).isEqualTo(BoatType.FISHING_BOAT);
        
        verify(boatRepository).findById(boatId);
        verify(boatRepository).save(any(BoatEntity.class));
        verify(boatMapper).toDto(updatedEntity);
    }

    @Test
    void updateBoatType_WithInvalidBoatType_ShouldThrowIllegalArgumentException() {
        // Given
        Long boatId = 1L;
        BoatTypeUpdateDto typeUpdateDto = new BoatTypeUpdateDto("INVALID_TYPE");
        when(boatRepository.findById(boatId)).thenReturn(Optional.of(testBoatEntity));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> boatService.updateBoatType(boatId, typeUpdateDto));
        
        verify(boatRepository).findById(boatId);
        verify(boatRepository, never()).save(any());
        verify(boatMapper, never()).toDto(any());
    }

    @Test
    void updateBoatName_WhenSaveThrowsException_ShouldPropagateException() {
        // Given
        Long boatId = 1L;
        BoatNameUpdateDto nameUpdateDto = new BoatNameUpdateDto("New Name");
        when(boatRepository.findById(boatId)).thenReturn(Optional.of(testBoatEntity));
        when(boatRepository.save(any(BoatEntity.class))).thenThrow(new RuntimeException("Save error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> boatService.updateBoatName(boatId, nameUpdateDto));
        
        verify(boatRepository).findById(boatId);
        verify(boatRepository).save(any(BoatEntity.class));
        verify(boatMapper, never()).toDto(any());
    }

    @Test
    void updateBoatDescription_WhenSaveThrowsException_ShouldPropagateException() {
        // Given
        Long boatId = 1L;
        BoatDescriptionUpdateDto descriptionUpdateDto = new BoatDescriptionUpdateDto("New Description");
        when(boatRepository.findById(boatId)).thenReturn(Optional.of(testBoatEntity));
        when(boatRepository.save(any(BoatEntity.class))).thenThrow(new RuntimeException("Save error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> boatService.updateBoatDescription(boatId, descriptionUpdateDto));
        
        verify(boatRepository).findById(boatId);
        verify(boatRepository).save(any(BoatEntity.class));
        verify(boatMapper, never()).toDto(any());
    }

    @Test
    void updateBoatType_WhenSaveThrowsException_ShouldPropagateException() {
        // Given
        Long boatId = 1L;
        BoatTypeUpdateDto typeUpdateDto = new BoatTypeUpdateDto("YACHT");
        when(boatRepository.findById(boatId)).thenReturn(Optional.of(testBoatEntity));
        when(boatRepository.save(any(BoatEntity.class))).thenThrow(new RuntimeException("Save error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> boatService.updateBoatType(boatId, typeUpdateDto));
        
        verify(boatRepository).findById(boatId);
        verify(boatRepository).save(any(BoatEntity.class));
        verify(boatMapper, never()).toDto(any());
    }

    @Test
    void updateBoatName_WhenMapperThrowsException_ShouldPropagateException() {
        // Given
        Long boatId = 1L;
        BoatNameUpdateDto nameUpdateDto = new BoatNameUpdateDto("New Name");
        BoatEntity updatedEntity = new BoatEntity();
        updatedEntity.setId(1L);
        updatedEntity.setName("New Name");
        updatedEntity.setDescription("A test boat for unit testing");
        updatedEntity.setBoatType(BoatType.SAILBOAT);
        updatedEntity.setCreatedDate(testDateTime);
        updatedEntity.setUpdatedDate(testDateTime.plusHours(1));
        
        when(boatRepository.findById(boatId)).thenReturn(Optional.of(testBoatEntity));
        when(boatRepository.save(any(BoatEntity.class))).thenReturn(updatedEntity);
        when(boatMapper.toDto(updatedEntity)).thenThrow(new RuntimeException("Mapping error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> boatService.updateBoatName(boatId, nameUpdateDto));
        
        verify(boatRepository).findById(boatId);
        verify(boatRepository).save(any(BoatEntity.class));
        verify(boatMapper).toDto(updatedEntity);
    }

    @Test
    void updateBoatDescription_WhenMapperThrowsException_ShouldPropagateException() {
        // Given
        Long boatId = 1L;
        BoatDescriptionUpdateDto descriptionUpdateDto = new BoatDescriptionUpdateDto("New Description");
        BoatEntity updatedEntity = new BoatEntity();
        updatedEntity.setId(1L);
        updatedEntity.setName("Test Boat");
        updatedEntity.setDescription("New Description");
        updatedEntity.setBoatType(BoatType.SAILBOAT);
        updatedEntity.setCreatedDate(testDateTime);
        updatedEntity.setUpdatedDate(testDateTime.plusHours(1));
        
        when(boatRepository.findById(boatId)).thenReturn(Optional.of(testBoatEntity));
        when(boatRepository.save(any(BoatEntity.class))).thenReturn(updatedEntity);
        when(boatMapper.toDto(updatedEntity)).thenThrow(new RuntimeException("Mapping error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> boatService.updateBoatDescription(boatId, descriptionUpdateDto));
        
        verify(boatRepository).findById(boatId);
        verify(boatRepository).save(any(BoatEntity.class));
        verify(boatMapper).toDto(updatedEntity);
    }

    @Test
    void updateBoatType_WhenMapperThrowsException_ShouldPropagateException() {
        // Given
        Long boatId = 1L;
        BoatTypeUpdateDto typeUpdateDto = new BoatTypeUpdateDto("YACHT");
        BoatEntity updatedEntity = new BoatEntity();
        updatedEntity.setId(1L);
        updatedEntity.setName("Test Boat");
        updatedEntity.setDescription("A test boat for unit testing");
        updatedEntity.setBoatType(BoatType.YACHT);
        updatedEntity.setCreatedDate(testDateTime);
        updatedEntity.setUpdatedDate(testDateTime.plusHours(1));
        
        when(boatRepository.findById(boatId)).thenReturn(Optional.of(testBoatEntity));
        when(boatRepository.save(any(BoatEntity.class))).thenReturn(updatedEntity);
        when(boatMapper.toDto(updatedEntity)).thenThrow(new RuntimeException("Mapping error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> boatService.updateBoatType(boatId, typeUpdateDto));
        
        verify(boatRepository).findById(boatId);
        verify(boatRepository).save(any(BoatEntity.class));
        verify(boatMapper).toDto(updatedEntity);
    }
}