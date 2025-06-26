package com.surplusfood.ordermanagement.order_service.domain.service;

import com.surplusfood.ordermanagement.order_service.domain.model.common.UserId;
import org.springframework.stereotype.Service;

@Service
public class OrderValidationService {

    public void validateSellerIsNotBlocked(UserId sellerId) {
        // This would normally call a UserServiceClient or query a user read model
        // We'll hard-code logic here for now
        if ("blocked-seller-id".equals(sellerId.getValue())) {
            throw new IllegalStateException("Seller is blocked from receiving orders.");
        }
    }
}
