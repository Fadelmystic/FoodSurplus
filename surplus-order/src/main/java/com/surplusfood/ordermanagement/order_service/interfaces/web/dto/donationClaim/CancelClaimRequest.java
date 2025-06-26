package com.surplusfood.ordermanagement.order_service.interfaces.web.dto.donationClaim;

import jakarta.validation.constraints.NotNull;

public record CancelClaimRequest(
        @NotNull String claimId,
        String reason
) {}
