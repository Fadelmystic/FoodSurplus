package com.surplusfood.ordermanagement.order_service.application.query;

import com.surplusfood.ordermanagement.order_service.domain.model.common.UserId;
import com.surplusfood.ordermanagement.order_service.domain.model.donationclaim.ClaimStatus;

public record FindDonationClaimsByUserQuery(
        UserId userId,
        ClaimStatus status,
        int page,
        int size
) {}
