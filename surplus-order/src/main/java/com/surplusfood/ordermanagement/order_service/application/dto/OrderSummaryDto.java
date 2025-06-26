package com.surplusfood.ordermanagement.order_service.application.dto;

import java.time.LocalDateTime;

public record OrderSummaryDto(
        String orderId,
        String buyerId,
        String totalAmount,
        String currency,
        String status,
        LocalDateTime orderDate
) {}
