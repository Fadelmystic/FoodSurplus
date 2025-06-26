package com.surplusfood.ordermanagement.order_service.domain.model.order;

public enum OrderStatus {
    PENDING_PAYMENT,
    PENDING_CONFIRMATION,
    CONFIRMED,
    PREPARING,
    READY_FOR_PICKUP,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED
} 