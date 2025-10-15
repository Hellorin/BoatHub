package io.hellorin.boathub.controller;

import io.hellorin.boathub.dto.BoatDto;
import io.hellorin.boathub.dto.BoatTypeUpdateDto;
import io.hellorin.boathub.domain.BoatType;
import io.hellorin.boathub.service.BoatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testBoat.getId(), response.getBody().getId());
        assertEquals(testBoat.getName(), response.getBody().getName());
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
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
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
        updatedBoat.setBoatType(BoatType.MOTORBOAT);
        
        when(boatService.updateBoatType(boatId, typeUpdateDto)).thenReturn(Optional.of(updatedBoat));

        // When
        ResponseEntity<BoatDto> response = boatV1Controller.updateBoatType(boatId, typeUpdateDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(BoatType.MOTORBOAT, response.getBody().getBoatType());
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
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(boatService).updateBoatType(boatId, typeUpdateDto);
    }
}