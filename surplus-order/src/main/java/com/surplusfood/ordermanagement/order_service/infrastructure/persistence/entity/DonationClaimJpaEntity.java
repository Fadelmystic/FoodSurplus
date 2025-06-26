package com.surplusfood.ordermanagement.order_service.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "donation_claims")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationClaimJpaEntity {

    @Id
    @Column(name = "claim_id")
    private String id;

    @Column(name = "ngo_id", nullable = false)
    private String ngoId;

    @Column(name = "seller_id", nullable = false)
    private String sellerId;

    @Column(name = "claim_date", nullable = false)
    private LocalDateTime claimDate;

    @Column(name = "status", nullable = false)
    private String status;

    @OneToMany(
            mappedBy = "donationClaim",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<DonationItemJpaEntity> items;

    @Column(name = "pickup_street")
    private String pickupStreet;

    @Column(name = "pickup_city")
    private String pickupCity;

    @Column(name = "pickup_postal_code")
    private String pickupPostalCode;

    @Column(name = "pickup_country")
    private String pickupCountry;

    @Column(name = "pickup_instructions")
    private String pickupInstructions;

    @Column(name = "logistics_id")
    private String logisticsId;
}