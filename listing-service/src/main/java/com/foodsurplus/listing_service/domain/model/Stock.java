package com.foodsurplus.listing_service.domain.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Stock {
    private int quantite;
    private boolean disponible;

    protected Stock() {} // Pour JPA

    public Stock(int quantite) {
        this.quantite = quantite;
        this.disponible = quantite > 0;
    }

    public int getQuantite() { return quantite; }
    public boolean isDisponible() { return disponible; }

    public void verifierDisponibilite(int quantiteDemandee) {
        if (!this.disponible || this.quantite < quantiteDemandee) {
            throw new IllegalStateException("Le surplus alimentaire n'est pas disponible en quantité suffisante");
        }
    }

    public void mettreAJourQuantite(int quantiteCommandee) {
        if (quantiteCommandee > this.quantite) {
            throw new IllegalArgumentException("La quantité commandée dépasse la quantité disponible");
        }
        this.quantite -= quantiteCommandee;
        this.disponible = this.quantite > 0;
    }
} 