package com.surplusfood.ordermanagement.order_service.domain.event.donationClaim;

import com.surplusfood.ordermanagement.order_service.domain.model.donationclaim.ClaimId;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class DonationClaimApprovedEvent implements DonationClaimDomainEvent {
    ClaimId claimId;
    LocalDateTime timestamp;
}