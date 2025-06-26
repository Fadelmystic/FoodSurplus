package com.surplusfood.ordermanagement.order_service.application.command.order;

import com.surplusfood.ordermanagement.order_service.domain.model.order.OrderId;

public record CancelOrderCommand(OrderId orderId, String reason) {
}
