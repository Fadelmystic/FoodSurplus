package com.foodsurplus.listing_service.infrastructure.repository;

import com.foodsurplus.listing_service.domain.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ListingRepository extends JpaRepository<Listing, UUID> {
    List<Listing> findBySupplierId(UUID supplierId);
    
    @Query("SELECT l FROM Listing l WHERE l.stock.disponible = true")
    List<Listing> findByDisponibleTrue();
    
    @Query("SELECT l FROM Listing l WHERE l.informations.nom LIKE %:terme% OR l.informations.description LIKE %:terme%")
    List<Listing> findByNomContainingOrDescriptionContaining(@Param("terme") String terme);
} 