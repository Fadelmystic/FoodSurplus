package com.surplusfood.ordermanagement.order_service.domain.model.order;

import lombok.Value;
import java.util.UUID;

@Value
public class OrderItemId {
    String value;

    private OrderItemId(String value) {
        this.value = value;
    }

    public static OrderItemId newInstance() {
        return new OrderItemId(UUID.randomUUID().toString());
    }

    public static OrderItemId from(String value) {
        return new OrderItemId(value);
    }
} 