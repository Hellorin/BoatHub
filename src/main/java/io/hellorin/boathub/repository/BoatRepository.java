package io.hellorin.boathub.repository;

import io.hellorin.boathub.domain.BoatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

/**
 * Repository interface for BoatEntity operations.
 * Extends JpaRepository to provide basic CRUD operations.
 */
@Repository
public interface BoatRepository extends JpaRepository<BoatEntity, Long> {
    
    /**
     * Finds all boats in the system with pagination.
     * @param pageable The pagination information
     * @return Page of all boats
     */
    Page<BoatEntity> findAll(Pageable pageable);
}
