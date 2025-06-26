package com.surplusfood.ordermanagement.order_service.domain.model.common;

import lombok.Value;
import java.util.UUID;

@Value
public class UserId {
    String value;

    private UserId(String value) {
        this.value = value;
    }

    public static UserId newInstance() {
        return new UserId(UUID.randomUUID().toString());
    }

    public static UserId from(String value) {
        return new UserId(value);
    }
} 