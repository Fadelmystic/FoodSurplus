package com.surplusfood.ordermanagement.order_service.domain.model.donationclaim;

import lombok.Value;
import java.util.UUID;

@Value
public class ClaimId {
    String value;

    private ClaimId(String value) {
        this.value = value;
    }

    public static ClaimId newInstance() {
        return new ClaimId(UUID.randomUUID().toString());
    }

    public static ClaimId from(String value) {
        return new ClaimId(value);
    }
}