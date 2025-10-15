package io.hellorin.boathub.repository;

import io.hellorin.boathub.domain.BoatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for BoatEntity operations.
 * Extends JpaRepository to provide basic CRUD operations.
 */
@Repository
public interface BoatRepository extends JpaRepository<BoatEntity, Long> {
    
    /**
     * Finds all boats in the system.
     * @return List of all boats
     */
    List<BoatEntity> findAll();
}
