package io.hellorin.boathub.controller;

import io.hellorin.boathub.dto.BoatCreationDto;
import io.hellorin.boathub.dto.BoatDto;
import io.hellorin.boathub.dto.BoatUpdateDto;
import io.hellorin.boathub.service.BoatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST controller for boat-related API endpoints.
 * Handles HTTP requests for boat operations.
 */
@RestController
@RequestMapping("/api/v1/boats")
@Tag(name = "Boats", description = "API for managing boats")
public class BoatV1Controller {

    private final BoatService boatService;

    public BoatV1Controller(BoatService boatService) {
        this.boatService = boatService;
    }

    /**
     * Retrieves all boats in the system.
     * @return List of all boats
     */
    @Operation(
        summary = "Get all boats",
        description = "Retrieves a list of all boats in the system"
    )
    @ApiResponse(responseCode = "200", description = "List of boats retrieved successfully")
    @GetMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<BoatDto> getAllBoats() {
        return boatService.getAllBoats();
    }

    /**
     * Retrieves a specific boat by its ID.
     * @param id The ID of the boat to retrieve
     * @return ResponseEntity containing the boat DTO if found, or 404 if not found
     */
    @Operation(
        summary = "Get boat by ID",
        description = "Retrieves a specific boat using its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Boat found successfully"),
        @ApiResponse(responseCode = "404", description = "Boat not found")
    })
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<BoatDto> getBoatById(
            @Parameter(description = "Unique identifier of the boat", example = "1")
            @PathVariable("id") Long id) {
        var boat = boatService.getBoatById(id);
        
        return boat.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Creates a new boat in the system.
     * @param boatCreationDto The boat data to create
     * @return ResponseEntity containing the created boat DTO
     */
    @Operation(
        summary = "Create a new boat",
        description = "Creates a new boat with the provided information"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Boat created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid boat data provided")
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BoatDto> createBoat(
            @Parameter(description = "Boat data to create")
            @Valid @RequestBody BoatCreationDto boatCreationDto) {
        var createdBoat = boatService.createBoat(boatCreationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBoat);
    }

    /**
     * Updates an existing boat by its ID.
     * @param id The ID of the boat to update
     * @param boatUpdateDto The updated boat data
     * @return ResponseEntity containing the updated boat DTO if found, or 404 if not found
     */
    @Operation(
        summary = "Update a boat",
        description = "Updates an existing boat with the provided information"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Boat updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid boat data provided"),
        @ApiResponse(responseCode = "404", description = "Boat not found")
    })
    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BoatDto> updateBoat(
            @Parameter(description = "Unique identifier of the boat to update", example = "1")
            @PathVariable("id") Long id,
            @Parameter(description = "Updated boat data")
            @Valid @RequestBody BoatUpdateDto boatUpdateDto) {
        var updatedBoat = boatService.updateBoat(id, boatUpdateDto);
        
        return updatedBoat.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes a boat by its ID.
     * @param id The ID of the boat to delete
     * @return ResponseEntity with no content if successful, or 404 if not found
     */
    @Operation(
        summary = "Delete a boat",
        description = "Removes a boat from the system by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Boat deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Boat not found")
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteBoat(
            @Parameter(description = "Unique identifier of the boat to delete", example = "1")
            @PathVariable("id") Long id) {
        boolean deleted = boatService.deleteBoat(id);
        
        return deleted ? ResponseEntity.noContent().build() 
                      : ResponseEntity.notFound().build();
    }
    
}
