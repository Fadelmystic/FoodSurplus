package com.surplusfood.ordermanagement.order_service.domain.model.donationclaim;

import com.surplusfood.ordermanagement.order_service.domain.model.common.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public class DonationItem {
    @EmbeddedId
    private DonationItemId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_claim_id", nullable = false)
    private DonationClaim donationClaim;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "listing_id", nullable = false))
    private ListingId listingId;

    @Column(nullable = false)
    private String productName;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "quantity_claimed_value")),
        @AttributeOverride(name = "unit", column = @Column(name = "quantity_claimed_unit"))
    })
    private Quantity quantityClaimed;

    public DonationItem(DonationClaim donationClaim, ListingId listingId, String productName, Quantity quantityClaimed) {
        this.id = DonationItemId.random();
        this.donationClaim = donationClaim;
        this.listingId = listingId;
        this.productName = productName;
        this.quantityClaimed = quantityClaimed;
    }

    public void updateQuantityClaimed(Quantity newQuantity) {
        if (newQuantity == null || newQuantity.getValue() <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantityClaimed = newQuantity;
    }

    public void reduceQuantity(Quantity requestedQuantity) {
        this.quantityClaimed = this.quantityClaimed.subtract(requestedQuantity);
    }

    public void validateQuantityNotExceeded(Quantity requested, Quantity available) {
        if (available.subtract(requested).getValue() < 0) {
            throw new IllegalArgumentException("Requested quantity exceeds availability.");
        }
    }

    public static DonationItem rehydrate(
            DonationItemId id,
            String productName,
            Quantity quantity,
            ListingId listingId
    ) {
        DonationItem item = new DonationItem();
        item.id = id; // ✅ Corrected field name
        item.productName = productName;
        item.quantityClaimed = quantity; // ✅ Corrected field name
        item.listingId = listingId;
        return item;
    }

    void setDonationClaim(DonationClaim claim) {
        this.donationClaim = claim;
    }


} 