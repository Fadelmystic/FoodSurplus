package com.surplusfood.ordermanagement.order_service.infrastructure.persistence.repository;

import com.surplusfood.ordermanagement.order_service.domain.model.common.UserId;
import com.surplusfood.ordermanagement.order_service.domain.model.order.Order;
import com.surplusfood.ordermanagement.order_service.domain.model.order.OrderId;
import com.surplusfood.ordermanagement.order_service.domain.model.order.OrderStatus;
import com.surplusfood.ordermanagement.order_service.domain.repository.OrderRepository;
import com.surplusfood.ordermanagement.order_service.infrastructure.persistence.mapper.OrderPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaOrderRepository implements OrderRepository {

    private final SpringDataOrderRepository springDataRepo;
    private final OrderPersistenceMapper mapper;

    @Override
    public void save(Order order) {
        springDataRepo.save(mapper.toJpaEntity(order));
    }

    @Override
    public Optional<Order> findById(OrderId orderId) {
        return springDataRepo.findById(orderId.getValue())
                .map(mapper::toDomainEntity);
    }

    @Override
    public List<Order> findByUserIdAndStatus(UserId userId, OrderStatus status, int page, int size) {
        var pageResult = springDataRepo.findByBuyerIdAndStatus(
                userId.getValue(), status.name(), Pageable.ofSize(size).withPage(page));
        return pageResult.stream()
                .map(mapper::toDomainEntity)
                .toList();
    }

}
