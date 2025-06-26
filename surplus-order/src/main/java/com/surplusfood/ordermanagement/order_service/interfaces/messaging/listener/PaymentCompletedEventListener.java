package com.surplusfood.ordermanagement.order_service.interfaces.messaging.listener;

import com.surplusfood.ordermanagement.order_service.domain.model.order.Order;
import com.surplusfood.ordermanagement.order_service.domain.model.order.OrderId;
import com.surplusfood.ordermanagement.order_service.domain.model.order.PaymentId;
import com.surplusfood.ordermanagement.order_service.domain.repository.OrderRepository;
import com.surplusfood.ordermanagement.order_service.interfaces.messaging.dto.PaymentEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentCompletedEventListener {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "payment-events", groupId = "order-service")
    @Transactional
    public void listen(PaymentEventPayload payload) {
        log.info("Received payment event: {}", payload);

        if (!"COMPLETED".equalsIgnoreCase(payload.getStatus())) {
            log.warn("Ignoring non-completed payment status: {}", payload.getStatus());
            return;
        }

        var order = orderRepository.findById(OrderId.from(payload.getOrderId()))
                .orElseThrow(() -> new IllegalStateException("Order not found: " + payload.getOrderId()));

        order.confirmPayment(PaymentId.from(payload.getPaymentId()));
        orderRepository.save(order);

        log.info("Order {} marked as paid", payload.getOrderId());
    }
}
