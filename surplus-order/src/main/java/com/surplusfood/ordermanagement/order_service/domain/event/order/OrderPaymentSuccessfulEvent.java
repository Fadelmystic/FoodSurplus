package com.surplusfood.ordermanagement.order_service.domain.event.order;

import lombok.Value;

import java.time.LocalDateTime;

import com.surplusfood.ordermanagement.order_service.domain.model.order.OrderId;
import com.surplusfood.ordermanagement.order_service.domain.model.order.PaymentId;

@Value
public class OrderPaymentSuccessfulEvent implements OrderDomainEvent {
    OrderId orderId;
    PaymentId paymentId;
    LocalDateTime timestamp;
}
