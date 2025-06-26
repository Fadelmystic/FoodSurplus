package com.surplusfood.ordermanagement.order_service.interfaces.web.dto.donationClaim;

import com.surplusfood.ordermanagement.order_service.application.dto.AddressDto;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PlaceClaimRequest(
        @NotNull String ngoId,
        @NotNull String sellerId,
        @NotNull AddressDto pickupAddress,
        String pickupInstructions,
        @NotNull List<DonationItemRequest> items
) {
    public record DonationItemRequest(
            @NotNull String listingId,
            @NotNull String productName,
            @NotNull int quantity
    ) {}
}
