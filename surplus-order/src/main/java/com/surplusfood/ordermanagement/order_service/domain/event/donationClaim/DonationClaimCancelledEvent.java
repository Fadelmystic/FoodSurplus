package com.surplusfood.ordermanagement.order_service.domain.event.donationClaim;

import com.surplusfood.ordermanagement.order_service.domain.model.donationclaim.ClaimId;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class DonationClaimCancelledEvent implements DonationClaimDomainEvent {
    ClaimId claimId;
    String reason;
    LocalDateTime timestamp;
}