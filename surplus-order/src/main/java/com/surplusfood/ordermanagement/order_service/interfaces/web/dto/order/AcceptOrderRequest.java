package com.surplusfood.ordermanagement.order_service.interfaces.web.dto.order;

import jakarta.validation.constraints.NotNull;

public record AcceptOrderRequest(
        @NotNull String orderId
) {}
