package com.surplusfood.ordermanagement.order_service.application.command.order;

import com.surplusfood.ordermanagement.order_service.domain.model.order.CurrencyCode;
import com.surplusfood.ordermanagement.order_service.domain.model.order.OrderId;

public record ApplyOrderDiscountCommand(
        OrderId orderId,
        String discountAmount,
        CurrencyCode currency
) {}

