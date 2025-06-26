package com.surplusfood.ordermanagement.order_service.domain.model.common;

import lombok.Value;
import java.util.UUID;

@Value
public class LogisticsId {
    String value;

    private LogisticsId(String value) {
        this.value = value;
    }

    public static LogisticsId newInstance() {
        return new LogisticsId(UUID.randomUUID().toString());
    }

    public static LogisticsId from(String value) {
        return new LogisticsId(value);
    }
} 