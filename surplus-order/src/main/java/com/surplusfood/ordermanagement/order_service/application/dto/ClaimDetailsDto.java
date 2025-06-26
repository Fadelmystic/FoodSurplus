package com.surplusfood.ordermanagement.order_service.application.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ClaimDetailsDto(
        String claimId,
        String ngoId,
        String sellerId,
        AddressDto pickupAddress,
        String pickupInstructions,
        String status,
        LocalDateTime claimDate,
        List<DonationClaimItemDto> claimedItems
) {}
