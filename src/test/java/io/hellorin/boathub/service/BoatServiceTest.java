package io.hellorin.boathub.service;

import io.hellorin.boathub.domain.BoatEntity;
import io.hellorin.boathub.domain.BoatType;
import io.hellorin.boathub.dto.BoatDto;
import io.hellorin.boathub.mapper.BoatMapper;
import io.hellorin.boathub.repository.BoatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void getAllBoats_WhenBoatsExist_ShouldReturnListOfBoats() {
        // Given
        var boatEntity2 = new BoatEntity();
        boatEntity2.setId(2L);
        boatEntity2.setName("Another Boat");
        boatEntity2.setDescription("Another test boat");
        boatEntity2.setBoatType(BoatType.MOTORBOAT);
        boatEntity2.setCreatedDate(testDateTime);
        boatEntity2.setUpdatedDate(testDateTime);

        var boatDto2 = new BoatDto();
        boatDto2.setId(2L);
        boatDto2.setName("Another Boat");
        boatDto2.setDescription("Another test boat");
        boatDto2.setBoatType(BoatType.MOTORBOAT);
        boatDto2.setCreatedDate(testDateTime);
        boatDto2.setUpdatedDate(testDateTime);

        var boatEntities = Arrays.asList(testBoatEntity, boatEntity2);
        var expectedDtos = Arrays.asList(testBoatDto, boatDto2);

        when(boatRepository.findAll()).thenReturn(boatEntities);
        when(boatMapper.toDto(testBoatEntity)).thenReturn(testBoatDto);
        when(boatMapper.toDto(boatEntity2)).thenReturn(boatDto2);

        // When
        var result = boatService.getAllBoats();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedDtos, result);
        
        verify(boatRepository).findAll();
        verify(boatMapper).toDto(testBoatEntity);
        verify(boatMapper).toDto(boatEntity2);
    }

    @Test
    void getAllBoats_WhenNoBoatsExist_ShouldReturnEmptyList() {
        // Given
        when(boatRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        var result = boatService.getAllBoats();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(boatRepository).findAll();
        verify(boatMapper, never()).toDto(any());
    }

    @Test
    void getAllBoats_WhenRepositoryThrowsException_ShouldPropagateException() {
        // Given
        when(boatRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> boatService.getAllBoats());
        
        verify(boatRepository).findAll();
        verify(boatMapper, never()).toDto(any());
    }

    @Test
    void getBoatById_WhenBoatExists_ShouldReturnBoatDto() {
        // Given
        var boatId = 1L;
        when(boatRepository.findById(boatId)).thenReturn(Optional.of(testBoatEntity));
        when(boatMapper.toDto(testBoatEntity)).thenReturn(testBoatDto);

        // When
        var result = boatService.getBoatById(boatId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testBoatDto, result.get());
        
        verify(boatRepository).findById(boatId);
        verify(boatMapper).toDto(testBoatEntity);
    }

    @Test
    void getBoatById_WhenBoatDoesNotExist_ShouldReturnEmptyOptional() {
        // Given
        var boatId = 999L;
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
        var boatId = 1L;
        when(boatRepository.findById(boatId)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> boatService.getBoatById(boatId));
        
        verify(boatRepository).findById(boatId);
        verify(boatMapper, never()).toDto(any());
    }

    @Test
    void getBoatById_WhenMapperThrowsException_ShouldPropagateException() {
        // Given
        var boatId = 1L;
        when(boatRepository.findById(boatId)).thenReturn(Optional.of(testBoatEntity));
        when(boatMapper.toDto(testBoatEntity)).thenThrow(new RuntimeException("Mapping error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> boatService.getBoatById(boatId));
        
        verify(boatRepository).findById(boatId);
        verify(boatMapper).toDto(testBoatEntity);
    }

    @Test
    void getAllBoats_WhenMapperReturnsNull_ShouldHandleGracefully() {
        // Given
        when(boatRepository.findAll()).thenReturn(Arrays.asList(testBoatEntity));
        when(boatMapper.toDto(testBoatEntity)).thenReturn(null);

        // When
        var result = boatService.getAllBoats();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0));
        
        verify(boatRepository).findAll();
        verify(boatMapper).toDto(testBoatEntity);
    }

    @Test
    void getBoatById_WithDifferentBoatTypes_ShouldMapCorrectly() {
        // Given
        var fishingBoatEntity = new BoatEntity();
        fishingBoatEntity.setId(2L);
        fishingBoatEntity.setName("Fishing Vessel");
        fishingBoatEntity.setDescription("A boat for fishing");
        fishingBoatEntity.setBoatType(BoatType.FISHING_BOAT);
        fishingBoatEntity.setCreatedDate(testDateTime);
        fishingBoatEntity.setUpdatedDate(testDateTime);

        var fishingBoatDto = new BoatDto();
        fishingBoatDto.setId(2L);
        fishingBoatDto.setName("Fishing Vessel");
        fishingBoatDto.setDescription("A boat for fishing");
        fishingBoatDto.setBoatType(BoatType.FISHING_BOAT);
        fishingBoatDto.setCreatedDate(testDateTime);
        fishingBoatDto.setUpdatedDate(testDateTime);

        var boatId = 2L;
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
    void getAllBoats_WithMultipleBoatTypes_ShouldReturnAllBoats() {
        // Given
        var yachtEntity = new BoatEntity();
        yachtEntity.setId(3L);
        yachtEntity.setName("Luxury Yacht");
        yachtEntity.setDescription("A luxurious yacht");
        yachtEntity.setBoatType(BoatType.YACHT);
        yachtEntity.setCreatedDate(testDateTime);
        yachtEntity.setUpdatedDate(testDateTime);

        var yachtDto = new BoatDto();
        yachtDto.setId(3L);
        yachtDto.setName("Luxury Yacht");
        yachtDto.setDescription("A luxurious yacht");
        yachtDto.setBoatType(BoatType.YACHT);
        yachtDto.setCreatedDate(testDateTime);
        yachtDto.setUpdatedDate(testDateTime);

        var boatEntities = Arrays.asList(testBoatEntity, yachtEntity);
        var expectedDtos = Arrays.asList(testBoatDto, yachtDto);

        when(boatRepository.findAll()).thenReturn(boatEntities);
        when(boatMapper.toDto(testBoatEntity)).thenReturn(testBoatDto);
        when(boatMapper.toDto(yachtEntity)).thenReturn(yachtDto);

        // When
        var result = boatService.getAllBoats();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedDtos, result);
        
        // Verify all boat types are present
        List<BoatType> boatTypes = result.stream()
                .map(BoatDto::getBoatType)
                .toList();
        assertTrue(boatTypes.contains(BoatType.SAILBOAT));
        assertTrue(boatTypes.contains(BoatType.YACHT));
        
        verify(boatRepository).findAll();
        verify(boatMapper).toDto(testBoatEntity);
        verify(boatMapper).toDto(yachtEntity);
    }
}