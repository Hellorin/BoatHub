package io.hellorin.boathub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hellorin.boathub.config.JpaConfiguration;
import io.hellorin.boathub.domain.BoatType;
import io.hellorin.boathub.dto.*;
import io.hellorin.boathub.service.BoatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Slice test for BoatV1Controller focusing on HTTP status codes.
 * Tests the controller layer in isolation with mocked service dependencies.
 */
@WebMvcTest(
    value = BoatV1Controller.class,
    excludeAutoConfiguration = {
            JpaConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class , DataSourceAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class, HibernateJpaAutoConfiguration.class
    })
class BoatV1ControllerSliceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BoatService boatService;

    @Autowired
    private ObjectMapper objectMapper;

    private BoatDto createTestBoat() {
        BoatDto boat = new BoatDto();
        boat.setId(1L);
        boat.setName("Test Boat");
        boat.setDescription("A test boat");
        boat.setBoatType(BoatType.SAILBOAT.name());
        boat.setCreatedDate(LocalDateTime.now());
        boat.setUpdatedDate(LocalDateTime.now());
        return boat;
    }

    // GET /api/v1/boats - Test HTTP status codes

    @Test
    void getAllBoatsInPage_WithValidParameters_ShouldReturn200() throws Exception {
        // Given
        List<BoatDto> boats = Arrays.asList(createTestBoat());
        Page<BoatDto> boatPage = new PageImpl<>(boats);
        when(boatService.getAllBoatsInPage(any(PageRequest.class))).thenReturn(boatPage);

        // When & Then
        mockMvc.perform(get("/api/v1/boats")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "name")
                .param("sortDirection", "asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Test Boat"));
    }

    @Test
    void getAllBoatsInPage_WithInvalidPage_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/boats")
                .param("page", "-1")
                .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllBoatsInPage_WithInvalidSize_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/boats")
                .param("page", "0")
                .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllBoatsInPage_WithExcessiveSize_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/boats")
                .param("page", "0")
                .param("size", "100"))
                .andExpect(status().isBadRequest());
    }

    // GET /api/v1/boats/{id} - Test HTTP status codes

    @Test
    void getBoatById_WhenBoatExists_ShouldReturn200() throws Exception {
        // Given
        BoatDto boat = createTestBoat();
        when(boatService.getBoatById(1L)).thenReturn(Optional.of(boat));

        // When & Then
        mockMvc.perform(get("/api/v1/boats/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Boat"));
    }

    @Test
    void getBoatById_WhenBoatNotFound_ShouldReturn404() throws Exception {
        // Given
        when(boatService.getBoatById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/boats/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBoatById_WithInvalidId_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/boats/invalid"))
                .andExpect(status().isBadRequest());
    }

    // POST /api/v1/boats - Test HTTP status codes

    @Test
    void createBoat_WithValidData_ShouldReturn201() throws Exception {
        // Given
        BoatCreationDto creationDto = new BoatCreationDto("New Boat", "A new boat", "SAILBOAT");
        BoatDto createdBoat = createTestBoat();
        createdBoat.setName("New Boat");
        when(boatService.createBoat(any(BoatCreationDto.class))).thenReturn(createdBoat);

        // When & Then
        mockMvc.perform(post("/api/v1/boats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creationDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string("Location", "/api/v1/boats/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Boat"));
    }

    @Test
    void createBoat_WithInvalidJson_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/boats")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBoat_WithMissingRequiredFields_ShouldReturn400() throws Exception {
        // Given
        BoatCreationDto invalidDto = new BoatCreationDto("", "", "");

        // When & Then
        mockMvc.perform(post("/api/v1/boats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBoat_WithInvalidBoatType_ShouldReturn400() throws Exception {
        // Given
        BoatCreationDto invalidDto = new BoatCreationDto("Test Boat", "A test boat", "INVALID_TYPE");

        // When & Then
        mockMvc.perform(post("/api/v1/boats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBoat_WithExcessiveNameLength_ShouldReturn400() throws Exception {
        // Given
        String longName = "a".repeat(101); // Exceeds 100 character limit
        BoatCreationDto invalidDto = new BoatCreationDto(longName, "A test boat", "SAILBOAT");

        // When & Then
        mockMvc.perform(post("/api/v1/boats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBoat_WithExcessiveDescriptionLength_ShouldReturn400() throws Exception {
        // Given
        String longDescription = "a".repeat(501); // Exceeds 500 character limit
        BoatCreationDto invalidDto = new BoatCreationDto("Test Boat", longDescription, "SAILBOAT");

        // When & Then
        mockMvc.perform(post("/api/v1/boats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    // PATCH /api/v1/boats/{id}/name - Test HTTP status codes

    @Test
    void updateBoatName_WithValidData_ShouldReturn200() throws Exception {
        // Given
        BoatNameUpdateDto nameUpdateDto = new BoatNameUpdateDto("Updated Name");
        BoatDto updatedBoat = createTestBoat();
        updatedBoat.setName("Updated Name");
        when(boatService.updateBoatName(any(Long.class), any(BoatNameUpdateDto.class))).thenReturn(Optional.of(updatedBoat));

        // When & Then
        mockMvc.perform(patch("/api/v1/boats/1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nameUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void updateBoatName_WhenBoatNotFound_ShouldReturn404() throws Exception {
        // Given
        BoatNameUpdateDto nameUpdateDto = new BoatNameUpdateDto("Updated Name");
        when(boatService.updateBoatName(999L, nameUpdateDto)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(patch("/api/v1/boats/999/name")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nameUpdateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBoatName_WithEmptyName_ShouldReturn400() throws Exception {
        // Given
        BoatNameUpdateDto invalidDto = new BoatNameUpdateDto("");

        // When & Then
        mockMvc.perform(patch("/api/v1/boats/1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBoatName_WithExcessiveNameLength_ShouldReturn400() throws Exception {
        // Given
        String longName = "a".repeat(101); // Exceeds 100 character limit
        BoatNameUpdateDto invalidDto = new BoatNameUpdateDto(longName);

        // When & Then
        mockMvc.perform(patch("/api/v1/boats/1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    // PATCH /api/v1/boats/{id}/description - Test HTTP status codes

    @Test
    void updateBoatDescription_WithValidData_ShouldReturn200() throws Exception {
        // Given
        BoatDescriptionUpdateDto descriptionUpdateDto = new BoatDescriptionUpdateDto("Updated description");
        BoatDto updatedBoat = createTestBoat();
        updatedBoat.setDescription("Updated description");
        when(boatService.updateBoatDescription(any(Long.class), any(BoatDescriptionUpdateDto.class))).thenReturn(Optional.of(updatedBoat));

        // When & Then
        mockMvc.perform(patch("/api/v1/boats/1/description")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(descriptionUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    void updateBoatDescription_WhenBoatNotFound_ShouldReturn404() throws Exception {
        // Given
        BoatDescriptionUpdateDto descriptionUpdateDto = new BoatDescriptionUpdateDto("Updated description");
        when(boatService.updateBoatDescription(999L, descriptionUpdateDto)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(patch("/api/v1/boats/999/description")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(descriptionUpdateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBoatDescription_WithExcessiveLength_ShouldReturn400() throws Exception {
        // Given
        String longDescription = "a".repeat(501); // Exceeds 500 character limit
        BoatDescriptionUpdateDto invalidDto = new BoatDescriptionUpdateDto(longDescription);

        // When & Then
        mockMvc.perform(patch("/api/v1/boats/1/description")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    // PATCH /api/v1/boats/{id}/type - Test HTTP status codes

    @Test
    void updateBoatType_WithValidData_ShouldReturn200() throws Exception {
        // Given
        BoatTypeUpdateDto typeUpdateDto = new BoatTypeUpdateDto("MOTORBOAT");
        BoatDto updatedBoat = createTestBoat();
        updatedBoat.setBoatType(BoatType.MOTORBOAT.name());
        when(boatService.updateBoatType(any(Long.class), any(BoatTypeUpdateDto.class))).thenReturn(Optional.of(updatedBoat));

        // When & Then
        mockMvc.perform(patch("/api/v1/boats/1/type")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(typeUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.boatType").value("MOTORBOAT"));
    }

    @Test
    void updateBoatType_WhenBoatNotFound_ShouldReturn404() throws Exception {
        // Given
        BoatTypeUpdateDto typeUpdateDto = new BoatTypeUpdateDto("MOTORBOAT");
        when(boatService.updateBoatType(999L, typeUpdateDto)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(patch("/api/v1/boats/999/type")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(typeUpdateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBoatType_WithInvalidBoatType_ShouldReturn400() throws Exception {
        // Given
        BoatTypeUpdateDto invalidDto = new BoatTypeUpdateDto("INVALID_TYPE");

        // When & Then
        mockMvc.perform(patch("/api/v1/boats/1/type")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    // DELETE /api/v1/boats/{id} - Test HTTP status codes

    @Test
    void deleteBoat_WhenBoatExists_ShouldReturn204() throws Exception {
        // Given
        when(boatService.deleteBoat(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/v1/boats/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBoat_WhenBoatNotFound_ShouldReturn404() throws Exception {
        // Given
        when(boatService.deleteBoat(999L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/v1/boats/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBoat_WithInvalidId_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/boats/invalid"))
                .andExpect(status().isBadRequest());
    }

    // Test unsupported HTTP methods

    @Test
    void putRequest_ShouldReturn405() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/boats/1"))
                .andExpect(status().isMethodNotAllowed());
    }


    // Test content type validation

    @Test
    void createBoat_WithWrongContentType_ShouldReturn415() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/boats")
                .contentType(MediaType.TEXT_PLAIN)
                .content("some text"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void updateBoatName_WithWrongContentType_ShouldReturn415() throws Exception {
        // When & Then
        mockMvc.perform(patch("/api/v1/boats/1/name")
                .contentType(MediaType.TEXT_PLAIN)
                .content("some text"))
                .andExpect(status().isUnsupportedMediaType());
    }
}
