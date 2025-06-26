package com.surplusfood.ordermanagement.order_service.application.dto;

public record OrderItemDto(
        String listingId,
        String productName,
        int quantity,
        String quantityUnit,
        String unitPrice,
        String currency,
        String itemSubtotal
) {}
