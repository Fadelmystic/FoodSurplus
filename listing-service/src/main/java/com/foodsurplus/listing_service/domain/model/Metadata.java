package com.foodsurplus.listing_service.domain.model;

import jakarta.persistence.Embeddable;
import java.time.Instant;

@Embeddable
public class Metadata {
    private Instant dateCreation;
    private Instant dateModification;

    public Metadata() {
        this.dateCreation = Instant.now();
        this.dateModification = Instant.now();
    }

    public Instant getDateCreation() { return dateCreation; }
    public Instant getDateModification() { return dateModification; }

    public void mettreAJour() {
        this.dateModification = Instant.now();
    }
} 