package com.surplusfood.ordermanagement.order_service.infrastructure.persistence.repository;

import com.surplusfood.ordermanagement.order_service.infrastructure.persistence.entity.OrderJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataOrderRepository extends JpaRepository<OrderJpaEntity, String> {
    Page<OrderJpaEntity> findByBuyerIdAndStatus(String ngoId, String status, Pageable pageable);
}
