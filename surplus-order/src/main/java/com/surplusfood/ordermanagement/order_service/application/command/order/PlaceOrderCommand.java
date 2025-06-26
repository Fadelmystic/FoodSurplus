package com.surplusfood.ordermanagement.order_service.application.command.order;

import com.surplusfood.ordermanagement.order_service.domain.model.common.Address;
import com.surplusfood.ordermanagement.order_service.domain.model.common.UserId;
import com.surplusfood.ordermanagement.order_service.domain.model.order.CurrencyCode;

import java.math.BigDecimal;
import java.util.List;

public record PlaceOrderCommand(
        UserId buyerId,
        UserId sellerId,
        Address shippingAddress,
        List<OrderItemData> items,
        CurrencyCode currency
) {
    public record OrderItemData(
            String listingId,
            String productName,
            int quantity,
            BigDecimal unitPriceAmount // e.g., "10.99"
    ) {}
}
