package com.foodsurplus.listing_service.domain.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "listings")
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Embedded
    private InformationsListing informations;
    
    @Embedded
    private Stock stock;
    
    @Embedded
    private Metadata metadata;
    
    @Column(name = "supplier_id")
    private UUID supplierId;

    @Enumerated(EnumType.STRING)
    private Category category;
    private String unit; // "kg", "g", "piece", etc.

    // Constructeurs
    protected Listing() {} // Pour JPA

    public Listing(InformationsListing informations, Stock stock, UUID supplierId) {
        this.informations = informations;
        this.stock = stock;
        this.supplierId = supplierId;
        this.metadata = new Metadata();
    }

    public Listing(InformationsListing informations, Stock stock, UUID supplierId, Category category, String unit) {
        this.informations = informations;
        this.stock = stock;
        this.supplierId = supplierId;
        this.category = category;
        this.unit = unit;
        this.metadata = new Metadata();
    }

    // Getters
    public UUID getId() { return id; }
    public InformationsListing getInformations() { return informations; }
    public Stock getStock() { return stock; }
    public Metadata getMetadata() { return metadata; }
    public UUID getSupplierId() { return supplierId; }
    public Category getCategory() {
        return category;
    }

    // Méthodes métier
    public void mettreAJourInformations(InformationsListing nouvellesInformations) {
        this.informations = nouvellesInformations;
        this.metadata.mettreAJour();
    }

    public void mettreAJourStock(Stock nouveauStock) {
        this.stock = nouveauStock;
        this.metadata.mettreAJour();
    }

    public void verifierDisponibilite(int quantiteDemandee) {
        this.stock.verifierDisponibilite(quantiteDemandee);
    }

    public void mettreAJourQuantite(int quantiteCommandee) {
        this.stock.mettreAJourQuantite(quantiteCommandee);
        this.metadata.mettreAJour();
    }
} 