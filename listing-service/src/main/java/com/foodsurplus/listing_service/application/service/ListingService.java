package com.foodsurplus.listing_service.application.service;

import com.foodsurplus.listing_service.domain.model.Listing;
import com.foodsurplus.listing_service.domain.model.InformationsListing;
import com.foodsurplus.listing_service.domain.model.Stock;
import com.foodsurplus.listing_service.domain.model.Category;
import com.foodsurplus.listing_service.infrastructure.repository.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ListingService {
    private final ListingRepository listingRepository;

    @Autowired
    public ListingService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    // Opérations CRUD
    public Listing creerListing(
        String nom,
        String description,
        int quantite,
        String imageUrl,
        UUID supplierId,
        Category category,
        String unit
    ) {
        InformationsListing informations = new InformationsListing(nom, description, imageUrl);
        Stock stock = new Stock(quantite);
        Listing listing = new Listing(informations, stock, supplierId, category, unit);
        return listingRepository.save(listing);
    }

    public Listing obtenirListing(UUID id) {
        return listingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Listing non trouvé"));
    }

    public List<Listing> obtenirTousLesListings() {
        return listingRepository.findAll();
    }

    public Listing mettreAJourListing(UUID id, String nom, String description, int quantite, String imageUrl) {
        Listing listing = obtenirListing(id);
        InformationsListing nouvellesInformations = new InformationsListing(nom, description, imageUrl);
        Stock nouveauStock = new Stock(quantite);
        
        listing.mettreAJourInformations(nouvellesInformations);
        listing.mettreAJourStock(nouveauStock);
        
        return listingRepository.save(listing);
    }

    public void supprimerListing(UUID id) {
        listingRepository.deleteById(id);
    }

    // Méthodes métier spécifiques
    public List<Listing> obtenirListingsParSupplier(UUID supplierId) {
        return listingRepository.findBySupplierId(supplierId);
    }

    public List<Listing> obtenirListingsDisponibles() {
        return listingRepository.findByDisponibleTrue();
    }

    public List<Listing> rechercherListings(String termeRecherche) {
        return listingRepository.findByNomContainingOrDescriptionContaining(termeRecherche);
    }

    public void verifierDisponibilite(UUID listingId, int quantiteDemandee) {
        Listing listing = obtenirListing(listingId);
        listing.verifierDisponibilite(quantiteDemandee);
    }

    public void mettreAJourQuantite(UUID listingId, int quantiteCommandee) {
        Listing listing = obtenirListing(listingId);
        listing.mettreAJourQuantite(quantiteCommandee);
        listingRepository.save(listing);
    }
} 