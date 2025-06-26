package com.surplusfood.ordermanagement.order_service.domain.model.order;

import lombok.Value;
import java.util.UUID;

@Value
public class PaymentId {
    String value;

    private PaymentId(String value) {
        this.value = value;
    }

    public static PaymentId newInstance() {
        return new PaymentId(UUID.randomUUID().toString());
    }

    public static PaymentId from(String value) {
        return new PaymentId(value);
    }
} 