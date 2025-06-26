package com.surplusfood.ordermanagement.order_service.domain.model.common;

import lombok.Getter;
import lombok.Value;
import java.util.UUID;

@Getter
@Value
public class ListingId {
    String value;

    private ListingId(String value) {
        this.value = value;
    }

    public static ListingId newInstance() {
        return new ListingId(UUID.randomUUID().toString());
    }

    public static ListingId from(String value) {
        return new ListingId(value);
    }
} 