package com.surplusfood.ordermanagement.order_service.domain.model.order;

import lombok.Value;
import java.util.UUID;

@Value
public class OrderId {
    String value;

    private OrderId(String value) {
        this.value = value;
    }

    public static OrderId newInstance() {
        return new OrderId(UUID.randomUUID().toString());
    }

    public static OrderId from(String value) {
        return new OrderId(value);
    }
} 