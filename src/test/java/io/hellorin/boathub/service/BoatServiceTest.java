package io.hellorin.boathub.service;

import io.hellorin.boathub.domain.BoatEntity;
import io.hellorin.boathub.domain.BoatType;
import io.hellorin.boathub.dto.BoatDto;
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

import static org.junit.jupiter.api.Assertions.*;
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
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(expectedDtos, result.getContent());
        
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
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        
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
        assertTrue(result.isPresent());
        assertEquals(testBoatDto, result.get());
        
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
        assertTrue(result.isEmpty());
        
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
        assertTrue(result.isEmpty());
        
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
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertNull(result.getContent().get(0));
        
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
        assertTrue(result.isPresent());
        assertEquals(fishingBoatDto, result.get());
        assertEquals(BoatType.FISHING_BOAT, result.get().getBoatType());
        
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
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(expectedDtos, result.getContent());
        
        // Verify all boat types are present
        List<BoatType> boatTypes = result.getContent().stream()
                .map(BoatDto::getBoatType)
                .toList();
        assertTrue(boatTypes.contains(BoatType.SAILBOAT));
        assertTrue(boatTypes.contains(BoatType.YACHT));
        
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
        assertTrue(result.isPresent());
        assertEquals(updatedDto, result.get());
        assertEquals(newName, result.get().getName());
        
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
        assertTrue(result.isEmpty());
        
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
        assertTrue(result.isPresent());
        assertEquals(updatedDto, result.get());
        assertEquals(newDescription, result.get().getDescription());
        
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
        assertTrue(result.isEmpty());
        
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
        assertTrue(result.isPresent());
        assertEquals(updatedDto, result.get());
        assertEquals(newType, result.get().getBoatType());
        
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
        assertTrue(result.isEmpty());
        
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
}