package io.hellorin.boathub.service;

import io.hellorin.boathub.repository.BoatRepository;
import io.hellorin.boathub.mapper.BoatMapper;
import io.hellorin.boathub.domain.BoatEntity;
import io.hellorin.boathub.domain.BoatType;
import io.hellorin.boathub.dto.BoatCreationDto;
import io.hellorin.boathub.dto.BoatDto;
import io.hellorin.boathub.dto.BoatNameUpdateDto;
import io.hellorin.boathub.dto.BoatDescriptionUpdateDto;
import io.hellorin.boathub.dto.BoatTypeUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
     * Retrieves all boats from the system with pagination.
     * @param pageable The pagination information
     * @return Page of all boats as DTOs
     */
    public Page<BoatDto> getAllBoatsInPage(Pageable pageable) {
        return boatRepository.findAll(pageable)
                .map(boatMapper::toDto);
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
        BoatEntity boatEntity = boatMapper.toEntity(boatCreationDto);
        boatEntity.setCreatedDate(LocalDateTime.now());
        boatEntity.setUpdatedDate(LocalDateTime.now());
        
        BoatEntity savedEntity = boatRepository.save(boatEntity);
        return boatMapper.toDto(savedEntity);
    }


    /**
     * Updates the name of an existing boat by its ID.
     * @param id The ID of the boat to update
     * @param boatNameUpdateDto The new boat name
     * @return Optional containing the updated boat DTO if found, empty otherwise
     */
    public Optional<BoatDto> updateBoatName(Long id, BoatNameUpdateDto boatNameUpdateDto) {
        return boatRepository.findById(id)
                .map(existingBoat -> {
                    existingBoat.setName(boatNameUpdateDto.getName());
                    existingBoat.setUpdatedDate(LocalDateTime.now());
                    
                    BoatEntity savedEntity = boatRepository.save(existingBoat);
                    return boatMapper.toDto(savedEntity);
                });
    }

    /**
     * Updates the description of an existing boat by its ID.
     * @param id The ID of the boat to update
     * @param boatDescriptionUpdateDto The new boat description
     * @return Optional containing the updated boat DTO if found, empty otherwise
     */
    public Optional<BoatDto> updateBoatDescription(Long id, BoatDescriptionUpdateDto boatDescriptionUpdateDto) {
        return boatRepository.findById(id)
                .map(existingBoat -> {
                    existingBoat.setDescription(boatDescriptionUpdateDto.getDescription());
                    existingBoat.setUpdatedDate(LocalDateTime.now());
                    
                    BoatEntity savedEntity = boatRepository.save(existingBoat);
                    return boatMapper.toDto(savedEntity);
                });
    }

    /**
     * Updates the type of an existing boat by its ID.
     * @param id The ID of the boat to update
     * @param boatTypeUpdateDto The new boat type
     * @return Optional containing the updated boat DTO if found, empty otherwise
     */
    public Optional<BoatDto> updateBoatType(Long id, BoatTypeUpdateDto boatTypeUpdateDto) {
        return boatRepository.findById(id)
                .map(existingBoat -> {
                    // Convert String to BoatType enum
                    BoatType boatType = BoatType.valueOf(boatTypeUpdateDto.getBoatType().toUpperCase());
                    existingBoat.setBoatType(boatType);
                    existingBoat.setUpdatedDate(LocalDateTime.now());
                    
                    BoatEntity savedEntity = boatRepository.save(existingBoat);
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
