package io.hellorin.boathub.service;

import io.hellorin.boathub.repository.BoatRepository;
import io.hellorin.boathub.mapper.BoatMapper;
import io.hellorin.boathub.dto.BoatCreationDto;
import io.hellorin.boathub.dto.BoatDto;
import io.hellorin.boathub.dto.BoatUpdateDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for boat-related business operations.
 * Handles the conversion between entities and DTOs and coordinates with the repository.
 */
@Service
public class BoatService {

    private final BoatMapper boatMapper;

    private final BoatRepository boatRepository;

    public BoatService(BoatMapper boatMapper, BoatRepository boatRepository) {
        this.boatMapper = boatMapper;
        this.boatRepository = boatRepository;
    }

    /**
     * Retrieves all boats from the system.
     * @return List of all boats as DTOs
     */
    public List<BoatDto> getAllBoats() {
        return boatRepository.findAll().stream()
                .map(boatMapper::toDto)
                .toList();
    }

    /**
     * Retrieves a specific boat by its ID.
     * @param id The ID of the boat to retrieve
     * @return Optional containing the boat DTO if found, empty otherwise
     */
    public Optional<BoatDto> getBoatById(Long id) {
        return boatRepository.findById(id)
                .map(boatMapper::toDto);
    }

    /**
     * Creates a new boat in the system.
     * @param boatCreationDto The boat data to create
     * @return The created boat DTO with generated ID and timestamps
     */
    public BoatDto createBoat(BoatCreationDto boatCreationDto) {
        var boatEntity = boatMapper.toEntity(boatCreationDto);
        boatEntity.setCreatedDate(LocalDateTime.now());
        boatEntity.setUpdatedDate(LocalDateTime.now());
        
        var savedEntity = boatRepository.save(boatEntity);
        return boatMapper.toDto(savedEntity);
    }

    /**
     * Updates an existing boat by its ID.
     * @param id The ID of the boat to update
     * @param boatUpdateDto The updated boat data
     * @return Optional containing the updated boat DTO if found, empty otherwise
     */
    public Optional<BoatDto> updateBoat(Long id, BoatUpdateDto boatUpdateDto) {
        return boatRepository.findById(id)
                .map(existingBoat -> {
                    existingBoat.setName(boatUpdateDto.getName());
                    existingBoat.setDescription(boatUpdateDto.getDescription());
                    existingBoat.setBoatType(boatUpdateDto.getBoatType());
                    existingBoat.setUpdatedDate(LocalDateTime.now());
                    
                    var savedEntity = boatRepository.save(existingBoat);
                    return boatMapper.toDto(savedEntity);
                });
    }

    /**
     * Deletes a boat by its ID.
     * @param id The ID of the boat to delete
     * @return true if the boat was deleted, false if not found
     */
    public boolean deleteBoat(Long id) {
        if (boatRepository.existsById(id)) {
            boatRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
