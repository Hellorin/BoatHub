package io.hellorin.boathub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hellorin.boathub.config.CspConfig;
import io.hellorin.boathub.config.JpaConfiguration;
import io.hellorin.boathub.config.SecurityConfig;
import io.hellorin.boathub.domain.BoatType;
import io.hellorin.boathub.dto.*;
import io.hellorin.boathub.service.BoatService;
import io.hellorin.boathub.service.UserDetailsServiceImpl;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
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
    value = {BoatV1Controller.class, SecurityConfig.class, CspConfig.class},
    excludeAutoConfiguration = {
            JpaConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class , DataSourceAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class, HibernateJpaAutoConfiguration.class
    })
class BoatV1ControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BoatService boatService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

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

    /**
     * Helper method to add CSRF token to state-changing requests.
     * This simulates how the frontend would include CSRF tokens.
     */
    private static org.springframework.test.web.servlet.request.RequestPostProcessor csrf() {
        return SecurityMockMvcRequestPostProcessors.csrf();
    }

    // GET /api/v1/boats - Test HTTP status codes

    @Test
    @WithMockUser
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
    @WithMockUser
    void getAllBoatsInPage_WithInvalidPage_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/boats")
                .param("page", "-1")
                .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getAllBoatsInPage_WithInvalidSize_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/boats")
                .param("page", "0")
                .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getAllBoatsInPage_WithExcessiveSize_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/boats")
                .param("page", "0")
                .param("size", "100"))
                .andExpect(status().isBadRequest());
    }

    // GET /api/v1/boats/{id} - Test HTTP status codes

    @Test
    @WithMockUser
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
    @WithMockUser
    void getBoatById_WhenBoatNotFound_ShouldReturn404() throws Exception {
        // Given
        when(boatService.getBoatById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/boats/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getBoatById_WithInvalidId_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/boats/invalid"))
                .andExpect(status().isBadRequest());
    }

    // POST /api/v1/boats - Test HTTP status codes

    @Test
    @WithMockUser
    void createBoat_WithValidData_ShouldReturn201() throws Exception {
        // Given
        BoatCreationDto creationDto = new BoatCreationDto("New Boat", "A new boat", "SAILBOAT");
        BoatDto createdBoat = createTestBoat();
        createdBoat.setName("New Boat");
        when(boatService.createBoat(any(BoatCreationDto.class))).thenReturn(createdBoat);

        // When & Then
        mockMvc.perform(post("/api/v1/boats")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creationDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string("Location", "/api/v1/boats/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Boat"));
    }

    @Test
    @WithMockUser
    void createBoat_WithInvalidJson_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/boats")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void createBoat_WithMissingRequiredFields_ShouldReturn400() throws Exception {
        // Given
        BoatCreationDto invalidDto = new BoatCreationDto("", "", "");

        // When & Then
        mockMvc.perform(post("/api/v1/boats")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void createBoat_WithInvalidBoatType_ShouldReturn400() throws Exception {
        // Given
        BoatCreationDto invalidDto = new BoatCreationDto("Test Boat", "A test boat", "INVALID_TYPE");

        // When & Then
        mockMvc.perform(post("/api/v1/boats")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void createBoat_WithExcessiveNameLength_ShouldReturn400() throws Exception {
        // Given
        String longName = "a".repeat(101); // Exceeds 100 character limit
        BoatCreationDto invalidDto = new BoatCreationDto(longName, "A test boat", "SAILBOAT");

        // When & Then
        mockMvc.perform(post("/api/v1/boats")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void createBoat_WithExcessiveDescriptionLength_ShouldReturn400() throws Exception {
        // Given
        String longDescription = "a".repeat(501); // Exceeds 500 character limit
        BoatCreationDto invalidDto = new BoatCreationDto("Test Boat", longDescription, "SAILBOAT");

        // When & Then
        mockMvc.perform(post("/api/v1/boats")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    // PUT /api/v1/boats/{id} - Test HTTP status codes

    @Test
    @WithMockUser
    void updateBoat_WithValidData_ShouldReturn200() throws Exception {
        // Given
        BoatUpdateDto updateDto = new BoatUpdateDto("Updated Boat", "Updated description", "MOTORBOAT");
        BoatDto updatedBoat = createTestBoat();
        updatedBoat.setName("Updated Boat");
        updatedBoat.setDescription("Updated description");
        updatedBoat.setBoatType(BoatType.MOTORBOAT.name());
        when(boatService.updateBoat(any(Long.class), any(BoatUpdateDto.class))).thenReturn(Optional.of(updatedBoat));

        // When & Then
        mockMvc.perform(put("/api/v1/boats/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Boat"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.boatType").value("MOTORBOAT"));
    }

    @Test
    @WithMockUser
    void updateBoat_WhenBoatNotFound_ShouldReturn404() throws Exception {
        // Given
        BoatUpdateDto updateDto = new BoatUpdateDto("Updated Boat", "Updated description", "MOTORBOAT");
        when(boatService.updateBoat(999L, updateDto)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/v1/boats/999")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void updateBoat_WithInvalidJson_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/boats/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void updateBoat_WithMissingRequiredFields_ShouldReturn400() throws Exception {
        // Given
        BoatUpdateDto invalidDto = new BoatUpdateDto("", "", "");

        // When & Then
        mockMvc.perform(put("/api/v1/boats/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void updateBoat_WithInvalidBoatType_ShouldReturn400() throws Exception {
        // Given
        BoatUpdateDto invalidDto = new BoatUpdateDto("Test Boat", "A test boat", "INVALID_TYPE");

        // When & Then
        mockMvc.perform(put("/api/v1/boats/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void updateBoat_WithExcessiveNameLength_ShouldReturn400() throws Exception {
        // Given
        String longName = "a".repeat(101); // Exceeds 100 character limit
        BoatUpdateDto invalidDto = new BoatUpdateDto(longName, "A test boat", "SAILBOAT");

        // When & Then
        mockMvc.perform(put("/api/v1/boats/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void updateBoat_WithExcessiveDescriptionLength_ShouldReturn400() throws Exception {
        // Given
        String longDescription = "a".repeat(501); // Exceeds 500 character limit
        BoatUpdateDto invalidDto = new BoatUpdateDto("Test Boat", longDescription, "SAILBOAT");

        // When & Then
        mockMvc.perform(put("/api/v1/boats/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void updateBoat_WithInvalidId_ShouldReturn400() throws Exception {
        // Given
        BoatUpdateDto updateDto = new BoatUpdateDto("Updated Boat", "Updated description", "MOTORBOAT");

        // When & Then
        mockMvc.perform(put("/api/v1/boats/invalid")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void updateBoat_WithWrongContentType_ShouldReturn415() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/boats/1")
                .with(csrf())
                .contentType(MediaType.TEXT_PLAIN)
                .content("some text"))
                .andExpect(status().isUnsupportedMediaType());
    }

    // PATCH /api/v1/boats/{id}/name - Test HTTP status codes

    @Test
    @WithMockUser
    void updateBoatName_WithValidData_ShouldReturn200() throws Exception {
        // Given
        BoatNameUpdateDto nameUpdateDto = new BoatNameUpdateDto("Updated Name");
        BoatDto updatedBoat = createTestBoat();
        updatedBoat.setName("Updated Name");
        when(boatService.updateBoatName(any(Long.class), any(BoatNameUpdateDto.class))).thenReturn(Optional.of(updatedBoat));

        // When & Then
        mockMvc.perform(patch("/api/v1/boats/1/name")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nameUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    @WithMockUser
    void updateBoatName_WhenBoatNotFound_ShouldReturn404() throws Exception {
        // Given
        BoatNameUpdateDto nameUpdateDto = new BoatNameUpdateDto("Updated Name");
        when(boatService.updateBoatName(999L, nameUpdateDto)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(patch("/api/v1/boats/999/name")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nameUpdateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void updateBoatName_WithEmptyName_ShouldReturn400() throws Exception {
        // Given
        BoatNameUpdateDto invalidDto = new BoatNameUpdateDto("");

        // When & Then
        mockMvc.perform(patch("/api/v1/boats/1/name")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void updateBoatName_WithExcessiveNameLength_ShouldReturn400() throws Exception {
        // Given
        String longName = "a".repeat(101); // Exceeds 100 character limit
        BoatNameUpdateDto invalidDto = new BoatNameUpdateDto(longName);

        // When & Then
        mockMvc.perform(patch("/api/v1/boats/1/name")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    // PATCH /api/v1/boats/{id}/description - Test HTTP status codes

    @Test
    @WithMockUser
    void updateBoatDescription_WithValidData_ShouldReturn200() throws Exception {
        // Given
        BoatDescriptionUpdateDto descriptionUpdateDto = new BoatDescriptionUpdateDto("Updated description");
        BoatDto updatedBoat = createTestBoat();
        updatedBoat.setDescription("Updated description");
        when(boatService.updateBoatDescription(any(Long.class), any(BoatDescriptionUpdateDto.class))).thenReturn(Optional.of(updatedBoat));

        // When & Then
        mockMvc.perform(patch("/api/v1/boats/1/description")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(descriptionUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    @WithMockUser
    void updateBoatDescription_WhenBoatNotFound_ShouldReturn404() throws Exception {
        // Given
        BoatDescriptionUpdateDto descriptionUpdateDto = new BoatDescriptionUpdateDto("Updated description");
        when(boatService.updateBoatDescription(999L, descriptionUpdateDto)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(patch("/api/v1/boats/999/description")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(descriptionUpdateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void updateBoatDescription_WithExcessiveLength_ShouldReturn400() throws Exception {
        // Given
        String longDescription = "a".repeat(501); // Exceeds 500 character limit
        BoatDescriptionUpdateDto invalidDto = new BoatDescriptionUpdateDto(longDescription);

        // When & Then
        mockMvc.perform(patch("/api/v1/boats/1/description")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    // PATCH /api/v1/boats/{id}/type - Test HTTP status codes

    @Test
    @WithMockUser
    void updateBoatType_WithValidData_ShouldReturn200() throws Exception {
        // Given
        BoatTypeUpdateDto typeUpdateDto = new BoatTypeUpdateDto("MOTORBOAT");
        BoatDto updatedBoat = createTestBoat();
        updatedBoat.setBoatType(BoatType.MOTORBOAT.name());
        when(boatService.updateBoatType(any(Long.class), any(BoatTypeUpdateDto.class))).thenReturn(Optional.of(updatedBoat));

        // When & Then
        mockMvc.perform(patch("/api/v1/boats/1/type")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(typeUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.boatType").value("MOTORBOAT"));
    }

    @Test
    @WithMockUser
    void updateBoatType_WhenBoatNotFound_ShouldReturn404() throws Exception {
        // Given
        BoatTypeUpdateDto typeUpdateDto = new BoatTypeUpdateDto("MOTORBOAT");
        when(boatService.updateBoatType(999L, typeUpdateDto)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(patch("/api/v1/boats/999/type")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(typeUpdateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void updateBoatType_WithInvalidBoatType_ShouldReturn400() throws Exception {
        // Given
        BoatTypeUpdateDto invalidDto = new BoatTypeUpdateDto("INVALID_TYPE");

        // When & Then
        mockMvc.perform(patch("/api/v1/boats/1/type")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    // DELETE /api/v1/boats/{id} - Test HTTP status codes

    @Test
    @WithMockUser
    void deleteBoat_WhenBoatExists_ShouldReturn204() throws Exception {
        // Given
        when(boatService.deleteBoat(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/v1/boats/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void deleteBoat_WhenBoatNotFound_ShouldReturn404() throws Exception {
        // Given
        when(boatService.deleteBoat(999L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/v1/boats/999")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void deleteBoat_WithInvalidId_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/boats/invalid")
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    // CSRF Protection Tests - These tests verify CSRF protection is working

    @Test
    @WithMockUser
    void createBoat_WithoutCsrfToken_ShouldReturn403() throws Exception {
        // Given
        BoatCreationDto creationDto = new BoatCreationDto("New Boat", "A new boat", "SAILBOAT");

        // When & Then - Request without CSRF token should be rejected
        mockMvc.perform(post("/api/v1/boats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creationDto)))
                .andExpect(status().isFound());
    }

    @Test
    @WithMockUser
    void updateBoat_WithoutCsrfToken_ShouldReturn403() throws Exception {
        // Given
        BoatUpdateDto updateDto = new BoatUpdateDto("Updated Boat", "Updated description", "MOTORBOAT");

        // When & Then - Request without CSRF token should be rejected
        mockMvc.perform(put("/api/v1/boats/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isFound());
    }

    @Test
    @WithMockUser
    void updateBoatName_WithoutCsrfToken_ShouldReturn403() throws Exception {
        // Given
        BoatNameUpdateDto nameUpdateDto = new BoatNameUpdateDto("Updated Name");

        // When & Then - Request without CSRF token should be rejected
        mockMvc.perform(patch("/api/v1/boats/1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nameUpdateDto)))
                .andExpect(status().isFound());
    }

    @Test
    @WithMockUser
    void deleteBoat_WithoutCsrfToken_ShouldReturn403() throws Exception {
        // When & Then - Request without CSRF token should be rejected
        mockMvc.perform(delete("/api/v1/boats/1"))
                .andExpect(status().isFound());
    }

    // Test content type validation

    @Test
    @WithMockUser
    void createBoat_WithWrongContentType_ShouldReturn415() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/boats")
                .with(csrf())
                .contentType(MediaType.TEXT_PLAIN)
                .content("some text"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @WithMockUser
    void updateBoatName_WithWrongContentType_ShouldReturn415() throws Exception {
        // When & Then
        mockMvc.perform(patch("/api/v1/boats/1/name")
                .with(csrf())
                .contentType(MediaType.TEXT_PLAIN)
                .content("some text"))
                .andExpect(status().isUnsupportedMediaType());
    }

    // Authentication Protection Tests - These tests run WITHOUT @WithMockUser to verify authentication is required
    // Note: Unauthenticated requests return 403 (Forbidden) instead of 401

    @Test
    void getAllBoatsInPage_WithoutAuthentication_ShouldReturn403() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/boats")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getBoatById_WithoutAuthentication_ShouldReturn403() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/boats/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createBoat_WithoutAuthentication_ShouldReturn403() throws Exception {
        // Given
        BoatCreationDto creationDto = new BoatCreationDto("New Boat", "A new boat", "SAILBOAT");

        // When & Then
        mockMvc.perform(post("/api/v1/boats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creationDto)))
                .andExpect(status().isFound());
    }

    @Test
    void updateBoat_WithoutAuthentication_ShouldReturn403() throws Exception {
        // Given
        BoatUpdateDto updateDto = new BoatUpdateDto("Updated Boat", "Updated description", "MOTORBOAT");

        // When & Then
        mockMvc.perform(put("/api/v1/boats/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isFound());
    }

    @Test
    void updateBoatName_WithoutAuthentication_ShouldReturn403() throws Exception {
        // Given
        BoatNameUpdateDto nameUpdateDto = new BoatNameUpdateDto("Updated Name");

        // When & Then
        mockMvc.perform(patch("/api/v1/boats/1/name")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nameUpdateDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateBoatDescription_WithoutAuthentication_ShouldReturn403() throws Exception {
        // Given
        BoatDescriptionUpdateDto descriptionUpdateDto = new BoatDescriptionUpdateDto("Updated description");

        // When & Then
        mockMvc.perform(patch("/api/v1/boats/1/description")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(descriptionUpdateDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateBoatType_WithoutAuthentication_ShouldReturn403() throws Exception {
        // Given
        BoatTypeUpdateDto typeUpdateDto = new BoatTypeUpdateDto("MOTORBOAT");

        // When & Then
        mockMvc.perform(patch("/api/v1/boats/1/type")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(typeUpdateDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteBoat_WithoutAuthentication_ShouldReturn403() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/boats/1")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
