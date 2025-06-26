package com.surplusfood.ordermanagement.order_service.application.dto;

public record DonationClaimItemDto(
        String listingId,
        String productName,
        int quantity,
        String quantityUnit
) {}
