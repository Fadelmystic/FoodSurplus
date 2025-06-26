package com.surplusfood.ordermanagement.order_service.domain.event.order;

import com.surplusfood.ordermanagement.order_service.domain.model.common.LogisticsId;
import com.surplusfood.ordermanagement.order_service.domain.model.order.OrderId;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class OrderShippedEvent implements OrderDomainEvent {
    OrderId orderId;
    LogisticsId logisticsId;
    String trackingNumber;
    LocalDateTime timestamp;
}
