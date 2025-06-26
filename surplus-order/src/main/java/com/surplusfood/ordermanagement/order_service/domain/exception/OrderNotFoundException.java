package com.surplusfood.ordermanagement.order_service.domain.exception;

import com.surplusfood.ordermanagement.order_service.domain.model.order.OrderId;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(OrderId orderId) {
        super("Order not found with ID: " + orderId.getValue());
    }
}
