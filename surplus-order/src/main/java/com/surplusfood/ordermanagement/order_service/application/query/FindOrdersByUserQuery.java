package com.surplusfood.ordermanagement.order_service.application.query;

import com.surplusfood.ordermanagement.order_service.domain.model.common.UserId;
import com.surplusfood.ordermanagement.order_service.domain.model.order.OrderStatus;

public record FindOrdersByUserQuery(
        UserId userId,
        OrderStatus status, // Optional: can be null to fetch all
        int page,
        int size
) {}