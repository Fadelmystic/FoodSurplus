package com.surplusfood.ordermanagement.order_service.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "donation_claim_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationItemJpaEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String productName;

    @Column(name = "quantity_claimed_value", nullable = false)
    private int quantity;

    @Column(name = "quantity_claimed_unit", nullable = false)
    private String unit;

    @Column(name = "listing_id", nullable = false)
    private String listingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_claim_id", nullable = false)
    private DonationClaimJpaEntity donationClaim;


}
