package com.surplusfood.ordermanagement.order_service.infrastructure.persistence.mapper;

import com.surplusfood.ordermanagement.order_service.application.dto.AddressDto;
import com.surplusfood.ordermanagement.order_service.application.dto.ClaimDetailsDto;
import com.surplusfood.ordermanagement.order_service.application.dto.DonationClaimItemDto;
import com.surplusfood.ordermanagement.order_service.domain.model.common.*;
import com.surplusfood.ordermanagement.order_service.domain.model.donationclaim.ClaimStatus;
import com.surplusfood.ordermanagement.order_service.domain.model.donationclaim.ClaimId;
import com.surplusfood.ordermanagement.order_service.domain.model.donationclaim.DonationClaim;
import com.surplusfood.ordermanagement.order_service.domain.model.donationclaim.DonationItem;
import com.surplusfood.ordermanagement.order_service.domain.model.donationclaim.DonationItemId;
import com.surplusfood.ordermanagement.order_service.infrastructure.persistence.entity.DonationClaimJpaEntity;
import com.surplusfood.ordermanagement.order_service.infrastructure.persistence.entity.DonationItemJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class DonationClaimPersistenceMapper {

    public ClaimDetailsDto toClaimDetailsDto(DonationClaim claim) {
        if (claim == null) {
            throw new IllegalArgumentException("La réclamation ne peut pas être nulle");
        }

        List<DonationClaimItemDto> claimItems = claim.getClaimedItems().stream()
                .filter(Objects::nonNull)
                .map(item -> new DonationClaimItemDto(
                        Objects.requireNonNull(item.getListingId(), "listingId requis").getValue(),
                        Objects.requireNonNull(item.getProductName(), "productName requis"),
                        validateQuantity(item.getQuantityClaimed().getValue()),
                        Objects.requireNonNull(item.getQuantityClaimed().getUnit(), "unit requise")
                ))
                .toList();

        return new ClaimDetailsDto(
                Objects.requireNonNull(claim.getId(), "id requis").getValue(),
                Objects.requireNonNull(claim.getNgoId(), "ngoId requis").getValue(),
                Objects.requireNonNull(claim.getSellerId(), "sellerId requis").getValue(),
                new AddressDto(
                        claim.getPickupAddress().getStreet(),
                        claim.getPickupAddress().getCity(),
                        claim.getPickupAddress().getPostalCode(),
                        claim.getPickupAddress().getCountry()
                ),
                Objects.requireNonNull(claim.getPickupInstructions(), "instructions requises"),
                Objects.requireNonNull(claim.getStatus(), "status requis").name(),
                Objects.requireNonNull(claim.getClaimDate(), "date requise"),
                claimItems
        );
    }

    private int validateQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("La quantité ne peut pas être négative");
        }
        return quantity;
    }

    // --- Domain → JPA
    public DonationClaimJpaEntity toJpaEntity(DonationClaim donationClaim) {
        List<DonationItemJpaEntity> itemEntities = donationClaim.getClaimedItems().stream().map(item -> DonationItemJpaEntity.builder()
                .id(item.getId().getValue())
                .listingId(item.getListingId().getValue())
                .productName(item.getProductName())
                .quantity(item.getQuantityClaimed().getValue())
                .unit(item.getQuantityClaimed().getUnit())
                .donationClaim(null) // will be set below to avoid circular reference
                .build()
        ).toList();

        DonationClaimJpaEntity entity = DonationClaimJpaEntity.builder()
                .id(donationClaim.getId().getValue())
                .ngoId(donationClaim.getNgoId().getValue())
                .sellerId(donationClaim.getSellerId().getValue())
                .claimDate(donationClaim.getClaimDate())
                .status(donationClaim.getStatus().name())
                .pickupStreet(donationClaim.getPickupAddress().getStreet())
                .pickupCity(donationClaim.getPickupAddress().getCity())
                .pickupPostalCode(donationClaim.getPickupAddress().getPostalCode())
                .pickupCountry(donationClaim.getPickupAddress().getCountry())
                .pickupInstructions(donationClaim.getPickupInstructions())
                .logisticsId(donationClaim.getLogisticsId() != null ? donationClaim.getLogisticsId().getValue() : null)
                .build();

        itemEntities.forEach(i -> i.setDonationClaim(entity));
        entity.setItems(itemEntities);

        return entity;
    }

    public DonationClaim toDomainEntity(DonationClaimJpaEntity entity) {
        List<DonationItem> items = entity.getItems().stream()
                .map(i -> DonationItem.rehydrate(
                        DonationItemId.from(i.getId()),
                        i.getProductName(),
                        Quantity.of(i.getQuantity(), i.getUnit()), // Ensure correct unit if available
                        ListingId.from(i.getListingId())
                ))
                .toList();

        return DonationClaim.rehydrate(
                ClaimId.from(entity.getId()),
                UserId.from(entity.getNgoId()),
                UserId.from(entity.getSellerId()),
                entity.getClaimDate(),
                ClaimStatus.valueOf(entity.getStatus()),
                Address.of(
                        entity.getPickupStreet(),
                        entity.getPickupCity(),
                        entity.getPickupPostalCode(),
                        entity.getPickupCountry()
                ),
                entity.getPickupInstructions(),
                entity.getLogisticsId() != null ? LogisticsId.from(entity.getLogisticsId()) : null,
                items
        );
    }
}
