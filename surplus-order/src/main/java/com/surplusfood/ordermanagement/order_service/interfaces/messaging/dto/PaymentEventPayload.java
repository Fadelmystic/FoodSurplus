package com.surplusfood.ordermanagement.order_service.interfaces.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEventPayload {
    private String orderId;
    private String paymentId;
    private String status; // e.g., "COMPLETED", "FAILED"
    private String timestamp;
}
