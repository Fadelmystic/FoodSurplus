package com.surplusfood.ordermanagement.order_service.application.query;


import com.surplusfood.ordermanagement.order_service.domain.model.donationclaim.ClaimId;

public record FindClaimByIdQuery(ClaimId claimId) {}
