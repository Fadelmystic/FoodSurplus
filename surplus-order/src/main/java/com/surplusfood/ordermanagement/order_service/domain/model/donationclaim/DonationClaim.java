package com.surplusfood.ordermanagement.order_service.domain.model.donationclaim;

import com.surplusfood.ordermanagement.order_service.domain.event.donationClaim.*;
import com.surplusfood.ordermanagement.order_service.domain.model.common.*;
import com.surplusfood.ordermanagement.order_service.domain.exception.InvalidDonationClaimStatusException;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "donation_claims")
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = "claimedItems")
public class DonationClaim extends AbstractAggregateRoot<DonationClaim> {

    @EmbeddedId
    private ClaimId id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "ngo_id", nullable = false))
    private UserId ngoId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "seller_id", nullable = false))
    private UserId sellerId;

    @Column(name = "claim_date", nullable = false)
    private LocalDateTime claimDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status;

    @OneToMany(mappedBy = "donationClaim", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DonationItem> claimedItems = new ArrayList<>();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "pickup_street")),
            @AttributeOverride(name = "city", column = @Column(name = "pickup_city")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "pickup_postal_code")),
            @AttributeOverride(name = "country", column = @Column(name = "pickup_country"))
    })
    private Address pickupAddress;

    @Column(name = "pickup_instructions")
    private String pickupInstructions;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "logistics_id"))
    private LogisticsId logisticsId;

    @Version
    private int version;

    // Required for JPA
    protected DonationClaim() {}

    // ==== Factory Method ====
    public static DonationClaim create(
            UserId ngoId,
            UserId sellerId,
            Address pickupAddress,
            String pickupInstructions,
            List<DonationItemInput> itemInputs
    ) {
        if (ngoId == null || sellerId == null || pickupAddress == null) {
            throw new IllegalArgumentException("NGO, seller, and pickup address must not be null");
        }
        if (itemInputs == null || itemInputs.isEmpty()) {
            throw new IllegalArgumentException("At least one donation item must be provided");
        }

        DonationClaim claim = new DonationClaim();
        claim.id = ClaimId.newInstance();
        claim.ngoId = ngoId;
        claim.sellerId = sellerId;
        claim.claimDate = LocalDateTime.now();
        claim.status = ClaimStatus.PENDING_APPROVAL;
        claim.pickupAddress = pickupAddress;
        claim.pickupInstructions = pickupInstructions;

        for (DonationItemInput input : itemInputs) {
            DonationItem item = new DonationItem(claim, input.listingId(), input.productName(), input.quantity());
            claim.claimedItems.add(item);
        }

        claim.registerEvent(new DonationClaimSubmittedEvent(
                claim.id, ngoId, sellerId, claim.claimDate
        ));

        return claim;
    }

    // ==== Business Methods ====

    public void addClaimedItem(ListingId listingId, String productName, Quantity quantity) {
        requireStatus(ClaimStatus.PENDING_APPROVAL, "PENDING_APPROVAL");
        this.claimedItems.add(new DonationItem(this, listingId, productName, quantity));
    }

    public void approveClaim() {
        requireStatus(ClaimStatus.PENDING_APPROVAL, "APPROVED");
        this.status = ClaimStatus.APPROVED;
        registerEvent(new DonationClaimApprovedEvent(this.id, LocalDateTime.now()));
    }

    public void rejectClaim(String reason) {
        requireAnyStatus(List.of(ClaimStatus.PENDING_APPROVAL, ClaimStatus.APPROVED), "REJECTED");
        requireNonBlank(reason, "Rejection reason must not be blank");
        this.status = ClaimStatus.REJECTED;
        registerEvent(new DonationClaimRejectedEvent(this.id, reason, LocalDateTime.now()));
    }

    public void markAsReadyForPickup(LogisticsId logisticsId) {
        requireStatus(ClaimStatus.APPROVED, "READY_FOR_PICKUP");
        requireNotNull(logisticsId, "LogisticsId must not be null");
        this.logisticsId = logisticsId;
        this.status = ClaimStatus.READY_FOR_PICKUP;
        registerEvent(new DonationClaimReadyForPickupEvent(this.id, logisticsId, LocalDateTime.now()));
    }

    public void markAsCollected() {
        requireStatus(ClaimStatus.READY_FOR_PICKUP, "COLLECTED");
        this.status = ClaimStatus.COLLECTED;
        registerEvent(new DonationClaimCollectedEvent(this.id, LocalDateTime.now()));
    }

    public void cancelClaim(String reason) {
        requireAnyStatus(List.of(ClaimStatus.PENDING_APPROVAL, ClaimStatus.APPROVED), "CANCELLED");
        requireNonBlank(reason, "Cancellation reason must not be blank");
        this.status = ClaimStatus.CANCELLED;
        registerEvent(new DonationClaimCancelledEvent(this.id, reason, LocalDateTime.now()));
    }


    // ==== Rehydration Method ====
    public static DonationClaim rehydrate(
            ClaimId id,
            UserId ngoId,
            UserId sellerId,
            LocalDateTime claimDate,
            ClaimStatus status,
            Address pickupAddress,
            String pickupInstructions,
            LogisticsId logisticsId,
            List<DonationItem> claimedItems
    ) {
        DonationClaim claim = new DonationClaim();
        claim.id = id;
        claim.ngoId = ngoId;
        claim.sellerId = sellerId;
        claim.claimDate = claimDate;
        claim.status = status;
        claim.pickupAddress = pickupAddress;
        claim.pickupInstructions = pickupInstructions;
        claim.logisticsId = logisticsId;
        claim.claimedItems = claimedItems;

        claimedItems.forEach(item -> item.setDonationClaim(claim));
        return claim;
    }

    // ==== Required by Spring Data ====
    public boolean isNew() {
        return version == 0;
    }

    // ==== Helper Methods ====
    private void requireStatus(ClaimStatus expected, String attempted) {
        if (this.status != expected) {
            throw new InvalidDonationClaimStatusException(this.status.name(), attempted);
        }
    }

    private void requireAnyStatus(List<ClaimStatus> allowed, String attempted) {
        if (!allowed.contains(this.status)) {
            throw new InvalidDonationClaimStatusException(this.status.name(), attempted);
        }
    }

    private void requireNonBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    private void requireNotNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    // ==== Helper Record ====
    public record DonationItemInput(ListingId listingId, String productName, Quantity quantity) {}
}
