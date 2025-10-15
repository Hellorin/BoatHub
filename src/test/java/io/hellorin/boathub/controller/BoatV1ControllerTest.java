package io.hellorin.boathub.controller;

import io.hellorin.boathub.dto.*;
import io.hellorin.boathub.domain.BoatType;
import io.hellorin.boathub.service.BoatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoatV1ControllerTest {

    @Mock
    private BoatService boatService;

    @InjectMocks
    private BoatV1Controller boatV1Controller;

    private BoatDto testBoat;

    @BeforeEach
    void setUp() {
        testBoat = new BoatDto();
        testBoat.setId(1L);
        testBoat.setName("Test Boat");
        testBoat.setDescription("A test boat");
    }

    @Test
    void getBoatById_WhenBoatExists_ShouldReturnBoat() {
        // Given
        Long boatId = 1L;
        when(boatService.getBoatById(boatId)).thenReturn(Optional.of(testBoat));

        // When
        ResponseEntity<BoatDto> response = boatV1Controller.getBoatById(boatId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(testBoat.getId());
        assertThat(response.getBody().getName()).isEqualTo(testBoat.getName());
        verify(boatService).getBoatById(boatId);
    }

    @Test
    void getBoatById_WhenBoatDoesNotExist_ShouldReturnNotFound() {
        // Given
        Long boatId = 999L;
        when(boatService.getBoatById(boatId)).thenReturn(Optional.empty());

        // When
        ResponseEntity<BoatDto> response = boatV1Controller.getBoatById(boatId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(boatService).getBoatById(boatId);
    }

    @Test
    void updateBoatType_WithValidBoatType_ShouldReturnUpdatedBoat() {
        // Given
        Long boatId = 1L;
        BoatTypeUpdateDto typeUpdateDto = new BoatTypeUpdateDto("MOTORBOAT");
        BoatDto updatedBoat = new BoatDto();
        updatedBoat.setId(1L);
        updatedBoat.setName("Test Boat");
        updatedBoat.setDescription("A test boat");
        updatedBoat.setBoatType(BoatType.MOTORBOAT.name());
        
        when(boatService.updateBoatType(boatId, typeUpdateDto)).thenReturn(Optional.of(updatedBoat));

        // When
        ResponseEntity<BoatDto> response = boatV1Controller.updateBoatType(boatId, typeUpdateDto);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getBoatType()).isEqualTo(BoatType.MOTORBOAT.name());
        verify(boatService).updateBoatType(boatId, typeUpdateDto);
    }

    @Test
    void updateBoatType_WithNullBoatType_ShouldCallService() {
        // Given
        Long boatId = 1L;
        BoatTypeUpdateDto typeUpdateDto = new BoatTypeUpdateDto();
        when(boatService.updateBoatType(boatId, typeUpdateDto)).thenReturn(Optional.empty());

        // When
        ResponseEntity<BoatDto> response = boatV1Controller.updateBoatType(boatId, typeUpdateDto);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(boatService).updateBoatType(boatId, typeUpdateDto);
    }

    @Test
    void getAllBoatsInPage_WithValidParameters_ShouldReturnPageOfBoats() {
        // Given
        int page = 0;
        int size = 10;
        String sortBy = "name";
        String sortDirection = "asc";
        
        List<BoatDto> boats = Collections.singletonList(testBoat);
        Page<BoatDto> boatPage = new PageImpl<>(boats);
        PageRequest expectedPageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy));
        
        when(boatService.getAllBoatsInPage(expectedPageRequest)).thenReturn(boatPage);

        // When
        Page<BoatDto> result = boatV1Controller.getAllBoatsInPage(page, size, sortBy, sortDirection);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(testBoat.getId());
        verify(boatService).getAllBoatsInPage(expectedPageRequest);
    }

    @Test
    void getAllBoatsInPage_WithDescSortDirection_ShouldReturnPageWithDescSort() {
        // Given
        int page = 0;
        int size = 5;
        String sortBy = "id";
        String sortDirection = "desc";
        
        List<BoatDto> boats = Collections.singletonList(testBoat);
        Page<BoatDto> boatPage = new PageImpl<>(boats);
        PageRequest expectedPageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        
        when(boatService.getAllBoatsInPage(expectedPageRequest)).thenReturn(boatPage);

        // When
        Page<BoatDto> result = boatV1Controller.getAllBoatsInPage(page, size, sortBy, sortDirection);

        // Then
        assertThat(result).isNotNull();
        verify(boatService).getAllBoatsInPage(expectedPageRequest);
    }

    @Test
    void getAllBoatsInPage_WithNullSortDirection_ShouldDefaultToAsc() {
        // Given
        int page = 0;
        int size = 10;
        String sortBy = "name";
        String sortDirection = null;
        
        List<BoatDto> boats = Collections.singletonList(testBoat);
        Page<BoatDto> boatPage = new PageImpl<>(boats);
        PageRequest expectedPageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy));
        
        when(boatService.getAllBoatsInPage(expectedPageRequest)).thenReturn(boatPage);

        // When
        Page<BoatDto> result = boatV1Controller.getAllBoatsInPage(page, size, sortBy, sortDirection);

        // Then
        assertThat(result).isNotNull();
        verify(boatService).getAllBoatsInPage(expectedPageRequest);
    }

    @Test
    void createBoat_WithValidData_ShouldReturnCreatedBoat() {
        // Given
        BoatCreationDto creationDto = new BoatCreationDto("New Boat", "A new boat", "SAILBOAT");
        BoatDto createdBoat = new BoatDto();
        createdBoat.setId(2L);
        createdBoat.setName("New Boat");
        createdBoat.setDescription("A new boat");
        createdBoat.setBoatType(BoatType.SAILBOAT.name());
        
        when(boatService.createBoat(creationDto)).thenReturn(createdBoat);

        // When
        ResponseEntity<BoatDto> response = boatV1Controller.createBoat(creationDto);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(createdBoat.getId());
        assertThat(response.getBody().getName()).isEqualTo(createdBoat.getName());
        assertThat(response.getHeaders().getLocation().toString()).contains("/api/v1/boats/2");
        verify(boatService).createBoat(creationDto);
    }

    @Test
    void updateBoatName_WithValidData_ShouldReturnUpdatedBoat() {
        // Given
        Long boatId = 1L;
        BoatNameUpdateDto nameUpdateDto = new BoatNameUpdateDto("Updated Boat Name");
        BoatDto updatedBoat = new BoatDto();
        updatedBoat.setId(1L);
        updatedBoat.setName("Updated Boat Name");
        updatedBoat.setDescription("A test boat");
        
        when(boatService.updateBoatName(boatId, nameUpdateDto)).thenReturn(Optional.of(updatedBoat));

        // When
        ResponseEntity<BoatDto> response = boatV1Controller.updateBoatName(boatId, nameUpdateDto);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Updated Boat Name");
        verify(boatService).updateBoatName(boatId, nameUpdateDto);
    }

    @Test
    void updateBoatName_WhenBoatNotFound_ShouldReturnNotFound() {
        // Given
        Long boatId = 999L;
        BoatNameUpdateDto nameUpdateDto = new BoatNameUpdateDto("Updated Boat Name");
        when(boatService.updateBoatName(boatId, nameUpdateDto)).thenReturn(Optional.empty());

        // When
        ResponseEntity<BoatDto> response = boatV1Controller.updateBoatName(boatId, nameUpdateDto);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(boatService).updateBoatName(boatId, nameUpdateDto);
    }

    @Test
    void updateBoatDescription_WithValidData_ShouldReturnUpdatedBoat() {
        // Given
        Long boatId = 1L;
        BoatDescriptionUpdateDto descriptionUpdateDto = new BoatDescriptionUpdateDto("Updated description");
        BoatDto updatedBoat = new BoatDto();
        updatedBoat.setId(1L);
        updatedBoat.setName("Test Boat");
        updatedBoat.setDescription("Updated description");
        
        when(boatService.updateBoatDescription(boatId, descriptionUpdateDto)).thenReturn(Optional.of(updatedBoat));

        // When
        ResponseEntity<BoatDto> response = boatV1Controller.updateBoatDescription(boatId, descriptionUpdateDto);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDescription()).isEqualTo("Updated description");
        verify(boatService).updateBoatDescription(boatId, descriptionUpdateDto);
    }

    @Test
    void updateBoatDescription_WhenBoatNotFound_ShouldReturnNotFound() {
        // Given
        Long boatId = 999L;
        BoatDescriptionUpdateDto descriptionUpdateDto = new BoatDescriptionUpdateDto("Updated description");
        when(boatService.updateBoatDescription(boatId, descriptionUpdateDto)).thenReturn(Optional.empty());

        // When
        ResponseEntity<BoatDto> response = boatV1Controller.updateBoatDescription(boatId, descriptionUpdateDto);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(boatService).updateBoatDescription(boatId, descriptionUpdateDto);
    }

    @Test
    void updateBoatType_WhenBoatNotFound_ShouldReturnNotFound() {
        // Given
        Long boatId = 999L;
        BoatTypeUpdateDto typeUpdateDto = new BoatTypeUpdateDto("MOTORBOAT");
        when(boatService.updateBoatType(boatId, typeUpdateDto)).thenReturn(Optional.empty());

        // When
        ResponseEntity<BoatDto> response = boatV1Controller.updateBoatType(boatId, typeUpdateDto);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(boatService).updateBoatType(boatId, typeUpdateDto);
    }

    @Test
    void deleteBoat_WhenBoatExists_ShouldReturnNoContent() {
        // Given
        Long boatId = 1L;
        when(boatService.deleteBoat(boatId)).thenReturn(true);

        // When
        ResponseEntity<Void> response = boatV1Controller.deleteBoat(boatId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(boatService).deleteBoat(boatId);
    }

    @Test
    void deleteBoat_WhenBoatNotFound_ShouldReturnNotFound() {
        // Given
        Long boatId = 999L;
        when(boatService.deleteBoat(boatId)).thenReturn(false);

        // When
        ResponseEntity<Void> response = boatV1Controller.deleteBoat(boatId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(boatService).deleteBoat(boatId);
    }

    // Validation Tests for Controller Parameters

    @Test
    void getAllBoatsInPage_WithValidParameters_ShouldCallServiceWithCorrectParameters() {
        // Given
        int page = 0;
        int size = 10;
        String sortBy = "name";
        String sortDirection = "asc";
        Page<BoatDto> expectedPage = new PageImpl<>(Collections.singletonList(testBoat));
        when(boatService.getAllBoatsInPage(any(PageRequest.class))).thenReturn(expectedPage);

        // When
        Page<BoatDto> result = boatV1Controller.getAllBoatsInPage(page, size, sortBy, sortDirection);

        // Then
        assertThat(result.getContent()).hasSize(1);
        verify(boatService).getAllBoatsInPage(any(PageRequest.class));
    }

    @Test
    void getAllBoatsInPage_WithAllValidSortFields_ShouldCallService() {
        // Given
        String[] validSortFields = {"id", "name", "description", "boatType"};
        Page<BoatDto> expectedPage = new PageImpl<>(Collections.singletonList(testBoat));
        when(boatService.getAllBoatsInPage(any(PageRequest.class))).thenReturn(expectedPage);

        // When & Then
        for (String sortField : validSortFields) {
            Page<BoatDto> result = boatV1Controller.getAllBoatsInPage(0, 10, sortField, "asc");
            assertThat(result).isEqualTo(expectedPage);
        }
        
        // Verify the service was called the expected number of times
        verify(boatService, times(validSortFields.length)).getAllBoatsInPage(any(PageRequest.class));
    }

    @Test
    void getAllBoatsInPage_WithAllValidSortDirections_ShouldCallService() {
        // Given
        String[] validSortDirections = {"asc", "desc", "ASC", "DESC"};
        Page<BoatDto> expectedPage = new PageImpl<>(Collections.singletonList(testBoat));
        when(boatService.getAllBoatsInPage(any(PageRequest.class))).thenReturn(expectedPage);

        // When & Then
        for (String sortDirection : validSortDirections) {
            Page<BoatDto> result = boatV1Controller.getAllBoatsInPage(0, 10, "name", sortDirection);
            assertThat(result).isEqualTo(expectedPage);
        }
        
        // Verify the service was called the expected number of times
        verify(boatService, times(validSortDirections.length)).getAllBoatsInPage(any(PageRequest.class));
    }

    @Test
    void createBoat_WithValidData_ShouldCallService() {
        // Given
        BoatCreationDto boatDto = new BoatCreationDto("Test Boat", "A test boat", "SAILBOAT");
        when(boatService.createBoat(any(BoatCreationDto.class))).thenReturn(testBoat);

        // When
        ResponseEntity<BoatDto> response = boatV1Controller.createBoat(boatDto);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        verify(boatService).createBoat(boatDto);
    }

    @Test
    void createBoat_WithAllValidBoatTypes_ShouldCallService() {
        // Given
        String[] validBoatTypes = {"SAILBOAT", "MOTORBOAT", "YACHT", "SPEEDBOAT", "FISHING_BOAT", "OTHER"};
        when(boatService.createBoat(any(BoatCreationDto.class))).thenReturn(testBoat);

        // When & Then
        for (String boatType : validBoatTypes) {
            BoatCreationDto boatDto = new BoatCreationDto("Test Boat", "A test boat", boatType);
            ResponseEntity<BoatDto> response = boatV1Controller.createBoat(boatDto);
            
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
        }
        
        // Verify the service was called the expected number of times
        verify(boatService, times(validBoatTypes.length)).createBoat(any(BoatCreationDto.class));
    }

    @Test
    void createBoat_WithCaseInsensitiveBoatType_ShouldCallService() {
        // Given
        BoatCreationDto boatDto = new BoatCreationDto("Test Boat", "A test boat", "sailboat");
        when(boatService.createBoat(any(BoatCreationDto.class))).thenReturn(testBoat);

        // When
        ResponseEntity<BoatDto> response = boatV1Controller.createBoat(boatDto);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        verify(boatService).createBoat(boatDto);
    }

    @Test
    void updateBoatName_WithValidName_ShouldCallService() {
        // Given
        Long boatId = 1L;
        BoatNameUpdateDto nameDto = new BoatNameUpdateDto("Updated Name");
        when(boatService.updateBoatName(boatId, nameDto)).thenReturn(Optional.of(testBoat));

        // When
        ResponseEntity<BoatDto> response = boatV1Controller.updateBoatName(boatId, nameDto);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        verify(boatService).updateBoatName(boatId, nameDto);
    }

    @Test
    void updateBoatType_WithValidType_ShouldCallService() {
        // Given
        Long boatId = 1L;
        BoatTypeUpdateDto typeDto = new BoatTypeUpdateDto("MOTORBOAT");
        when(boatService.updateBoatType(boatId, typeDto)).thenReturn(Optional.of(testBoat));

        // When
        ResponseEntity<BoatDto> response = boatV1Controller.updateBoatType(boatId, typeDto);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        verify(boatService).updateBoatType(boatId, typeDto);
    }

    @Test
    void updateBoatType_WithAllValidBoatTypes_ShouldCallService() {
        // Given
        Long boatId = 1L;
        String[] validBoatTypes = {"SAILBOAT", "MOTORBOAT", "YACHT", "SPEEDBOAT", "FISHING_BOAT", "OTHER"};
        when(boatService.updateBoatType(anyLong(), any(BoatTypeUpdateDto.class))).thenReturn(Optional.of(testBoat));

        // When & Then
        for (String boatType : validBoatTypes) {
            BoatTypeUpdateDto typeDto = new BoatTypeUpdateDto(boatType);
            ResponseEntity<BoatDto> response = boatV1Controller.updateBoatType(boatId, typeDto);
            
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
        }
        
        // Verify the service was called the expected number of times
        verify(boatService, times(validBoatTypes.length)).updateBoatType(anyLong(), any(BoatTypeUpdateDto.class));
    }

    @Test
    void updateBoatType_WithCaseInsensitiveType_ShouldCallService() {
        // Given
        Long boatId = 1L;
        BoatTypeUpdateDto typeDto = new BoatTypeUpdateDto("yacht");
        when(boatService.updateBoatType(boatId, typeDto)).thenReturn(Optional.of(testBoat));

        // When
        ResponseEntity<BoatDto> response = boatV1Controller.updateBoatType(boatId, typeDto);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        verify(boatService).updateBoatType(boatId, typeDto);
    }
}