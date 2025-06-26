package com.surplusfood.ordermanagement.order_service.interfaces.web.dto.order;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PlaceOrderRequest(
        @NotNull String buyerId,
        @NotNull String sellerId,
        @NotNull AddressDto shippingAddress,
        @NotNull String currency,
        @NotNull List<OrderItemDto> items
) {
    public record OrderItemDto(
            @NotNull String listingId,
            @NotNull String productName,
            @NotNull int quantity,
            @NotNull double unitPriceAmount
    ) {}

    public record AddressDto(
            @NotNull String street,
            @NotNull String city,
            @NotNull String postalCode,
            @NotNull String country
    ) {}
}
