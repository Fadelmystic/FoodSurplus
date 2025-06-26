package com.surplusfood.ordermanagement.order_service.domain.event.order;

import com.surplusfood.ordermanagement.order_service.domain.model.common.UserId;
import com.surplusfood.ordermanagement.order_service.domain.model.order.Money;
import com.surplusfood.ordermanagement.order_service.domain.model.order.OrderId;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class OrderPlacedEvent implements OrderDomainEvent {
    OrderId orderId;
    UserId buyerId;
    UserId sellerId;
    Money totalAmount;
    LocalDateTime orderDate;
}
