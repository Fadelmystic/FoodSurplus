package com.surplusfood.ordermanagement.order_service.infrastructure.persistence.mapper;

import com.surplusfood.ordermanagement.order_service.application.dto.*;
import com.surplusfood.ordermanagement.order_service.domain.model.common.*;
import com.surplusfood.ordermanagement.order_service.domain.model.order.*;
import com.surplusfood.ordermanagement.order_service.infrastructure.persistence.entity.OrderItemJpaEntity;
import com.surplusfood.ordermanagement.order_service.infrastructure.persistence.entity.OrderJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderPersistenceMapper {


    // --- Domain → DTO
    public OrderDetailsDto toOrderDetailsDto(Order order) {
        AddressDto addressDto = new AddressDto(
                order.getShippingAddress().getStreet(),
                order.getShippingAddress().getCity(),
                order.getShippingAddress().getPostalCode(),
                order.getShippingAddress().getCountry()
        );

        List<OrderItemDto> itemDtos = order.getOrderItems().stream().map(item -> new OrderItemDto(
                item.getListingId().getValue(),
                item.getProductName(),
                item.getQuantity().getValue(),
                item.getQuantity().getUnit(),
                item.getUnitPrice().getAmount().toPlainString(),
                item.getUnitPrice().getCurrency().name(),
                item.getItemSubtotal().getAmount().toPlainString()
        )).toList();

        return new OrderDetailsDto(
                order.getOrderId().getValue(),
                order.getBuyerId().getValue(),
                order.getSellerId().getValue(),
                addressDto,
                itemDtos,
                order.getTotalAmount().getAmount().toPlainString(),
                order.getTotalAmount().getCurrency().name(),
                order.getDiscountApplied().getAmount().toPlainString(),
                order.getStatus().name(),
                order.getOrderDate()
        );
    }

    // --- Domain → JPA
    public OrderJpaEntity toJpaEntity(Order order) {
        List<OrderItemJpaEntity> itemEntities = order.getOrderItems().stream().map(item -> OrderItemJpaEntity.builder()
                .id(item.getOrderItemId().getValue())
                .listingId(item.getListingId().getValue())
                .productName(item.getProductName())
                .quantityValue(item.getQuantity().getValue())
                .quantityUnit(item.getQuantity().getUnit())
                .unitPriceAmount(item.getUnitPrice().getAmount())
                .unitPriceCurrency(item.getUnitPrice().getCurrency().name())
                .itemSubtotalAmount(item.getItemSubtotal().getAmount())
                .itemSubtotalCurrency(item.getItemSubtotal().getCurrency().name())
                .order(null) // will be set below to avoid circular reference
                .build()
        ).toList();

        OrderJpaEntity entity = OrderJpaEntity.builder()
                .id(order.getOrderId().getValue())
                .buyerId(order.getBuyerId().getValue())
                .sellerId(order.getSellerId().getValue())
                .orderDate(order.getOrderDate())
                .status(order.getStatus().name())
                .shippingStreet(order.getShippingAddress().getStreet())
                .shippingCity(order.getShippingAddress().getCity())
                .shippingPostalCode(order.getShippingAddress().getPostalCode())
                .shippingCountry(order.getShippingAddress().getCountry())
                .totalAmount(order.getTotalAmount().getAmount())
                .totalCurrency(order.getTotalAmount().getCurrency().name())
                .discountAppliedAmount(order.getDiscountApplied().getAmount())
                .discountAppliedCurrency(order.getDiscountApplied().getCurrency().name())
                .paymentId(order.getPaymentId() != null ? order.getPaymentId().getValue() : null)
                .logisticsId(order.getLogisticsId() != null ? order.getLogisticsId().getValue() : null)
                .items(null) // set below
                .build();

        itemEntities.forEach(i -> i.setOrder(entity));
        entity.setItems(itemEntities);

        return entity;
    }

    // --- JPA → Domain
    public Order toDomainEntity(OrderJpaEntity entity) {
        List<OrderItem> orderItems = entity.getItems().stream().map(i -> {
            OrderItem item = OrderItem.rehydrate(
                    OrderItemId.from(i.getId()),
                    ListingId.from(i.getListingId()),
                    i.getProductName(),
                    Quantity.of(i.getQuantityValue(), i.getQuantityUnit()),
                    Money.of(i.getUnitPriceAmount(), CurrencyCode.valueOf(i.getUnitPriceCurrency())),
                    Money.of(i.getItemSubtotalAmount(), CurrencyCode.valueOf(i.getItemSubtotalCurrency()))
            );
            return item;
        }).toList();

        Order order = Order.rehydrate(
                OrderId.from(entity.getId()),
                UserId.from(entity.getBuyerId()),
                UserId.from(entity.getSellerId()),
                entity.getOrderDate(),
                OrderStatus.valueOf(entity.getStatus()),
                Address.of(
                        entity.getShippingStreet(),
                        entity.getShippingCity(),
                        entity.getShippingPostalCode(),
                        entity.getShippingCountry()
                ),
                Money.of(entity.getTotalAmount(), CurrencyCode.valueOf(entity.getTotalCurrency())),
                Money.of(entity.getDiscountAppliedAmount(), CurrencyCode.valueOf(entity.getDiscountAppliedCurrency())),
                entity.getPaymentId() != null ? PaymentId.from(entity.getPaymentId()) : null,
                entity.getLogisticsId() != null ? LogisticsId.from(entity.getLogisticsId()) : null,
                orderItems
        );

        orderItems.forEach(item -> item.setOrder(order));
        return order;
    }

    public OrderSummaryDto toOrderSummaryDto(Order order) {
        return new OrderSummaryDto(
                order.getOrderId().getValue(),
                order.getStatus().name(),
                order.getSellerId().getValue(),
                order.getBuyerId().getValue(),
                order.getTotalAmount().getAmount().toPlainString(),
                order.getOrderDate()
        );
    }
}