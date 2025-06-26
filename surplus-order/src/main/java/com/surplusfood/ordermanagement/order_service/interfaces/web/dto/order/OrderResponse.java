package com.surplusfood.ordermanagement.order_service.interfaces.web.dto.order;

import java.util.List;

public record OrderResponse(
        String orderId,
        String buyerId,
        String sellerId,
        String status,
        String orderDate,
        AddressDto shippingAddress,
        List<OrderItemDto> items,
        double totalAmount,
        String currency
) {
    public record OrderItemDto(
            String productName,
            int quantity,
            double unitPrice
    ) {}

    public record AddressDto(
            String street,
            String city,
            String postalCode,
            String country
    ) {}
}
