package com.surplusfood.ordermanagement.order_service.domain.repository;

import com.surplusfood.ordermanagement.order_service.domain.model.common.UserId;
import com.surplusfood.ordermanagement.order_service.domain.model.order.Order;
import com.surplusfood.ordermanagement.order_service.domain.model.order.OrderId;
import com.surplusfood.ordermanagement.order_service.domain.model.order.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(OrderId orderId);
    List<Order> findByUserIdAndStatus(UserId userId, OrderStatus status, int page, int size);
}
