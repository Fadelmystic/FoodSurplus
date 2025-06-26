package com.surplusfood.ordermanagement.order_service.domain.exception;

import com.surplusfood.ordermanagement.order_service.domain.model.donationclaim.ClaimId;



public class DonationClaimNotFoundException extends RuntimeException {
    public DonationClaimNotFoundException(ClaimId claimId) {
        super("Order not found with ID: " + claimId.getValue());
    }
}
