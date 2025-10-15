package io.hellorin.boathub.controller;

import io.hellorin.boathub.dto.*;
import io.hellorin.boathub.service.BoatService;
import io.hellorin.boathub.validation.ValidSortDirection;
import io.hellorin.boathub.validation.ValidSortField;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

/**
 * REST controller for boat-related API endpoints.
 * Handles HTTP requests for boat operations.
 */
@RestController
@RequestMapping("/api/v1/boats")
@Tag(name = "Boats", description = "API for managing boats")
@Validated
public class BoatV1Controller {
    private final BoatService boatService;

    public BoatV1Controller(BoatService boatService) {
        this.boatService = boatService;
    }

    /**
     * Retrieves all boats in the system with pagination support.
     * @return Page of boats
     */
    @Operation(
        summary = "Get all boats in page",
        description = "Retrieves a paginated list of all boats in the system. Supports pagination parameters: page (0-based), size, sortBy (id, name, description, boatType), and sortDirection (asc, desc)."
    )
    @ApiResponse(responseCode = "200", description = "Page of boats retrieved successfully")
    @GetMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public Page<BoatDto> getAllBoatsInPage(
            @Parameter(description = "Page requested", example = "0")
            @Min(0) @RequestParam("page") int page,
            @Parameter(description = "Page size requested", example = "10")
            @Min(1) @Max(50) @RequestParam(value = "size", defaultValue = "10") int size,
            @Parameter(description = "Field to sort by", example = "name")
            @ValidSortField @RequestParam(name = "sortBy", defaultValue = "name", required = false) String sortBy,
            @Parameter(description = "Sort direction", example = "asc")
            @ValidSortDirection @RequestParam(name = "sortDirection", defaultValue = "asc", required = false) String sortDirection) {
        
        // Create sort direction
        Sort.Direction direction = parseSortDirection(sortDirection);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

        return boatService.getAllBoatsInPage(pageRequest);
    }
    
    /**
     * Parses the sort direction string into a Sort.Direction enum.
     * @param sortDirection The direction string (asc or desc)
     * @return Sort.Direction.ASC for "asc", Sort.Direction.DESC for "desc"
     */
    private Sort.Direction parseSortDirection(String sortDirection) {
        if (sortDirection == null || sortDirection.equalsIgnoreCase("asc")) {
            return Sort.Direction.ASC;
        } else {
            return Sort.Direction.DESC;
        }
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
        Optional<BoatDto> boat = boatService.getBoatById(id);
        
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

        return ResponseEntity.status(HttpStatus.CREATED)
                .location(java.net.URI.create("/api/v1/boats/" + createdBoat.getId()))
                .body(createdBoat);
    }


    /**
     * Updates the name of an existing boat by its ID.
     * @param id The ID of the boat to update
     * @param boatNameUpdateDto The new boat name
     * @return ResponseEntity containing the updated boat DTO if found, or 404 if not found
     */
    @Operation(
        summary = "Update boat name",
        description = "Updates the name of an existing boat"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Boat name updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid boat name provided"),
        @ApiResponse(responseCode = "404", description = "Boat not found")
    })
    @PatchMapping(value = "/{id}/name", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BoatDto> updateBoatName(
            @Parameter(description = "Unique identifier of the boat to update", example = "1")
            @PathVariable("id") Long id,
            @Parameter(description = "New boat name")
            @Valid @RequestBody BoatNameUpdateDto boatNameUpdateDto) {
        Optional<BoatDto> updatedBoat = boatService.updateBoatName(id, boatNameUpdateDto);
        
        return updatedBoat.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Updates the description of an existing boat by its ID.
     * @param id The ID of the boat to update
     * @param boatDescriptionUpdateDto The new boat description
     * @return ResponseEntity containing the updated boat DTO if found, or 404 if not found
     */
    @Operation(
        summary = "Update boat description",
        description = "Updates the description of an existing boat"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Boat description updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid boat description provided"),
        @ApiResponse(responseCode = "404", description = "Boat not found")
    })
    @PatchMapping(value = "/{id}/description", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BoatDto> updateBoatDescription(
            @Parameter(description = "Unique identifier of the boat to update", example = "1")
            @PathVariable("id") Long id,
            @Parameter(description = "New boat description")
            @Valid @RequestBody BoatDescriptionUpdateDto boatDescriptionUpdateDto) {
        Optional<BoatDto> updatedBoat = boatService.updateBoatDescription(id, boatDescriptionUpdateDto);
        
        return updatedBoat.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Updates the type of an existing boat by its ID.
     * @param id The ID of the boat to update
     * @param boatTypeUpdateDto The new boat type
     * @return ResponseEntity containing the updated boat DTO if found, or 404 if not found
     */
    @Operation(
        summary = "Update boat type",
        description = "Updates the type of an existing boat"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Boat type updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid boat type provided"),
        @ApiResponse(responseCode = "404", description = "Boat not found")
    })
    @PatchMapping(value = "/{id}/type", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BoatDto> updateBoatType(
            @Parameter(description = "Unique identifier of the boat to update", example = "1")
            @PathVariable("id") Long id,
            @Parameter(description = "New boat type")
            @Valid @RequestBody BoatTypeUpdateDto boatTypeUpdateDto) {
        var updatedBoat = boatService.updateBoatType(id, boatTypeUpdateDto);
        
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
