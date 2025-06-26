package com.surplusfood.ordermanagement.order_service.domain.event.donationClaim;

import com.surplusfood.ordermanagement.order_service.domain.model.common.UserId;
import com.surplusfood.ordermanagement.order_service.domain.model.donationclaim.ClaimId;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class DonationClaimSubmittedEvent implements DonationClaimDomainEvent {
    ClaimId claimId;
    UserId ngoId;
    UserId sellerId;
    LocalDateTime timestamp;
}