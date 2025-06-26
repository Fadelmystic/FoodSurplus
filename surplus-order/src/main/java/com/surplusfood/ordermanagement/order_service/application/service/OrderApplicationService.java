package com.surplusfood.ordermanagement.order_service.application.service;

import com.surplusfood.ordermanagement.order_service.application.command.order.*;
import com.surplusfood.ordermanagement.order_service.application.dto.OrderSummaryDto;
import com.surplusfood.ordermanagement.order_service.application.query.FindOrderByIdQuery;
import com.surplusfood.ordermanagement.order_service.application.dto.OrderDetailsDto;
import com.surplusfood.ordermanagement.order_service.application.query.FindOrdersByUserQuery;
import com.surplusfood.ordermanagement.order_service.domain.exception.OrderNotFoundException;
import com.surplusfood.ordermanagement.order_service.domain.model.common.ListingId;
import com.surplusfood.ordermanagement.order_service.domain.model.common.Quantity;
import com.surplusfood.ordermanagement.order_service.domain.model.order.Money;
import com.surplusfood.ordermanagement.order_service.domain.model.order.Order;
import com.surplusfood.ordermanagement.order_service.domain.repository.OrderRepository;
import com.surplusfood.ordermanagement.order_service.domain.service.OrderValidationService;
import com.surplusfood.ordermanagement.order_service.infrastructure.clients.ListingServiceClient;
import com.surplusfood.ordermanagement.order_service.infrastructure.persistence.mapper.OrderPersistenceMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderApplicationService {

    private final OrderRepository orderRepository;
    private final OrderPersistenceMapper orderMapper;
    private final OrderValidationService orderValidationService;
    private final ListingServiceClient listingServiceClient;

    // --- Use cases ---

    @Transactional
    public void handle(AcceptOrderCommand command) {
        var order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new OrderNotFoundException(command.orderId()));
        order.confirmOrder();
        orderRepository.save(order);
    }

    @Transactional
    public void handle(RejectOrderCommand command) {
        var order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new OrderNotFoundException(command.orderId()));
        order.rejectOrder(command.reason());
        orderRepository.save(order);
    }

    @Transactional
    public void handle(ConfirmOrderReceiptCommand command) {
        var order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new OrderNotFoundException(command.orderId()));
        order.markAsDelivered();
        orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public OrderDetailsDto handle(FindOrderByIdQuery query) {
        var order = orderRepository.findById(query.orderId())
                .orElseThrow(() -> new OrderNotFoundException(query.orderId()));
        return orderMapper.toOrderDetailsDto(order);
    }

    @Transactional
    public void handle(PlaceOrderCommand command) {
        // Validate seller is not blocked
        orderValidationService.validateSellerIsNotBlocked(command.sellerId());

        command.items().forEach(item -> {
            boolean available = listingServiceClient.isAvailable(item.listingId(), item.quantity());
            if (!available) {
                throw new IllegalStateException("Item " + item.listingId() + " is not available.");
            }
        });
        // Convert command-level item DTOs to domain-level item records
        var itemData = command.items().stream()
                .map(item -> new Order.OrderItemData(
                        ListingId.from(item.listingId()),
                        item.productName(),
                        Quantity.of(item.quantity(), "pcs"), // You'll want to make "pcs" dynamic if needed
                        Money.of(item.unitPriceAmount(), command.currency())
                ))
                .toList();

        // Place order
        var order = Order.placeOrder(
                command.buyerId(),
                command.sellerId(),
                command.shippingAddress(),
                itemData,
                command.currency()
        );

        orderRepository.save(order);
    }

    @Transactional
    public void handle(CancelOrderCommand command) {
        var order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new OrderNotFoundException(command.orderId()));

        order.cancelOrder(command.reason());
        orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<OrderSummaryDto> handle(FindOrdersByUserQuery query) {
        var allOrders = orderRepository.findByUserIdAndStatus(
                query.userId(), query.status(), query.page(), query.size()
        );
        return allOrders.stream()
                .map(orderMapper::toOrderSummaryDto)
                .toList();
    }

    @Transactional
    public void handle(ApplyOrderDiscountCommand command) {
        var order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new OrderNotFoundException(command.orderId()));

        var discount = Money.of(new BigDecimal(command.discountAmount()), command.currency());
        order.applyDiscount(discount);

        orderRepository.save(order);
    }
}
