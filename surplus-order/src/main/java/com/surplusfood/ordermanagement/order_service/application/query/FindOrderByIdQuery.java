package com.surplusfood.ordermanagement.order_service.application.query;

import com.surplusfood.ordermanagement.order_service.domain.model.order.OrderId;

public record FindOrderByIdQuery(OrderId orderId) {}
