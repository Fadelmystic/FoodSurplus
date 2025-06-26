package com.surplusfood.ordermanagement.order_service.domain.event.order;

import lombok.Value;

import java.time.LocalDateTime;

import com.surplusfood.ordermanagement.order_service.domain.model.order.OrderId;

@Value
public class OrderCancelledEvent implements OrderDomainEvent {
    OrderId orderId;
    String reason;
    LocalDateTime timestamp;
}
