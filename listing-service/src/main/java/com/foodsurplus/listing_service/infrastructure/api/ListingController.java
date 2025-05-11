package com.foodsurplus.listing_service.infrastructure.api;

import com.foodsurplus.listing_service.application.service.ListingService;
import com.foodsurplus.listing_service.domain.model.Listing;
import com.foodsurplus.listing_service.infrastructure.api.dto.CreerListingRequest;
import com.foodsurplus.listing_service.infrastructure.api.dto.MettreAJourListingRequest;
import com.foodsurplus.listing_service.infrastructure.api.dto.VerifierDisponibiliteRequest;
import com.foodsurplus.listing_service.infrastructure.api.dto.MettreAJourQuantiteRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.multipart.MultipartFile;
import com.foodsurplus.listing_service.application.service.ImageUploadService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/listings")
@Tag(name = "Listing", description = "API de gestion des surplus alimentaires")
public class ListingController {
    private final ListingService listingService;
    private final ImageUploadService imageUploadService;

    @Autowired
    public ListingController(ListingService listingService, ImageUploadService imageUploadService) {
        this.listingService = listingService;
        this.imageUploadService = imageUploadService;
    }

    @Operation(summary = "Créer un nouveau surplus alimentaire")
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Listing> creerListing(
        @RequestPart("data") CreerListingRequest request,
        @RequestPart("image") MultipartFile imageFile) throws IOException {
        String imageUrl = imageUploadService.uploadImage(imageFile);

        Listing listing = listingService.creerListing(
            request.getNom(),
            request.getDescription(),
            request.getQuantite(),
            imageUrl,
            request.getSupplierId(),
            request.getCategory(),
            request.getUnit()
        );
        return ResponseEntity.ok(listing);
    }

    @Operation(summary = "Obtenir un surplus alimentaire par son ID")
    @GetMapping("/{id}")
    public ResponseEntity<Listing> obtenirListing(
            @Parameter(description = "ID du surplus alimentaire") @PathVariable UUID id) {
        return ResponseEntity.ok(listingService.obtenirListing(id));
    }

    @Operation(summary = "Obtenir tous les surplus alimentaires")
    @GetMapping
    public ResponseEntity<List<Listing>> obtenirTousLesListings() {
        return ResponseEntity.ok(listingService.obtenirTousLesListings());
    }

    @Operation(summary = "Mettre à jour un surplus alimentaire")
    @PutMapping("/{id}")
    public ResponseEntity<Listing> mettreAJourListing(
            @Parameter(description = "ID du surplus alimentaire") @PathVariable UUID id,
            @RequestBody MettreAJourListingRequest request) {
        Listing listing = listingService.mettreAJourListing(
            id,
            request.getNom(),
            request.getDescription(),
            request.getQuantite(),
            request.getImageUrl()
        );
        return ResponseEntity.ok(listing);
    }

    @Operation(summary = "Supprimer un surplus alimentaire")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerListing(
            @Parameter(description = "ID du surplus alimentaire") @PathVariable UUID id) {
        listingService.supprimerListing(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtenir les surplus alimentaires d'un fournisseur")
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<Listing>> obtenirListingsParSupplier(
            @Parameter(description = "ID du fournisseur") @PathVariable UUID supplierId) {
        return ResponseEntity.ok(listingService.obtenirListingsParSupplier(supplierId));
    }

    @Operation(summary = "Obtenir les surplus alimentaires disponibles")
    @GetMapping("/disponibles")
    public ResponseEntity<List<Listing>> obtenirListingsDisponibles() {
        return ResponseEntity.ok(listingService.obtenirListingsDisponibles());
    }

    @Operation(summary = "Rechercher des surplus alimentaires")
    @GetMapping("/recherche")
    public ResponseEntity<List<Listing>> rechercherListings(
            @Parameter(description = "Terme de recherche") @RequestParam String terme) {
        return ResponseEntity.ok(listingService.rechercherListings(terme));
    }

    @Operation(summary = "Vérifier la disponibilité d'un surplus alimentaire")
    @PostMapping("/{id}/verifier-disponibilite")
    public ResponseEntity<Void> verifierDisponibilite(
            @Parameter(description = "ID du surplus alimentaire") @PathVariable UUID id,
            @RequestBody VerifierDisponibiliteRequest request) {
        listingService.verifierDisponibilite(id, request.getQuantiteDemandee());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Mettre à jour la quantité d'un surplus alimentaire")
    @PostMapping("/{id}/mettre-a-jour-quantite")
    public ResponseEntity<Void> mettreAJourQuantite(
            @Parameter(description = "ID du surplus alimentaire") @PathVariable UUID id,
            @RequestBody MettreAJourQuantiteRequest request) {
        listingService.mettreAJourQuantite(id, request.getQuantiteCommandee());
        return ResponseEntity.ok().build();
    }
} 