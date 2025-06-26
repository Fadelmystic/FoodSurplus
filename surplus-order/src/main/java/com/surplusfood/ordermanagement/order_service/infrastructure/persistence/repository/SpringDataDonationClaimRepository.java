package com.surplusfood.ordermanagement.order_service.infrastructure.persistence.repository;

import com.surplusfood.ordermanagement.order_service.infrastructure.persistence.entity.DonationClaimJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataDonationClaimRepository extends JpaRepository<DonationClaimJpaEntity, String> {
    Page<DonationClaimJpaEntity> findByNgoIdAndStatus(String buyerId, String status, Pageable pageable);
}
