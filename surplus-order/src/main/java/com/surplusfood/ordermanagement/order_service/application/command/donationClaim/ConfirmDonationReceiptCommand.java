package com.surplusfood.ordermanagement.order_service.application.command.donationClaim;

import com.surplusfood.ordermanagement.order_service.domain.model.donationclaim.ClaimId;

public record ConfirmDonationReceiptCommand(ClaimId claimId) {
}
