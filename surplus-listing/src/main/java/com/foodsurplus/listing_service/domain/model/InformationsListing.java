package com.foodsurplus.listing_service.domain.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class InformationsListing {
    private String nom;
    private String description;
    private String imageUrl;

    protected InformationsListing() {} // Pour JPA

    public InformationsListing(String nom, String description, String imageUrl) {
        this.nom = nom;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getNom() { return nom; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
} 