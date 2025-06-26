package com.surplusfood.ordermanagement.order_service.application.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailsDto(
        String orderId,
        String buyerId,
        String sellerId,
        AddressDto shippingAddress,
        List<OrderItemDto> items,
        String totalAmount,
        String currency,
        String discountApplied,
        String status,
        LocalDateTime orderDate
) {}
