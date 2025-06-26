package com.surplusfood.ordermanagement.order_service.application.command.donationClaim;

import com.surplusfood.ordermanagement.order_service.domain.model.common.Address;
import com.surplusfood.ordermanagement.order_service.domain.model.common.UserId;

import java.util.List;

public record PlaceClaimCommand(
        UserId ngoId,
        UserId sellerId,
        Address pickupAddress,
        String pickupInstructions,
        List<ClaimedItemData> items
) {
    public record ClaimedItemData(
            String listingId,
            String productName,
            int quantity
    ) {}
}


