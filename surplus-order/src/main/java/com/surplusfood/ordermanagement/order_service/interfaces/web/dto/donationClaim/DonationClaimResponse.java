package com.surplusfood.ordermanagement.order_service.interfaces.web.dto.donationClaim;

import java.util.List;

public record DonationClaimResponse(
        String claimId,
        String listingId,
        String ngoId,
        String status,
        List<DonationItemResponse> items
) {
    public record DonationItemResponse(
            String donationItemId,
            int quantity
    ) {}
}
