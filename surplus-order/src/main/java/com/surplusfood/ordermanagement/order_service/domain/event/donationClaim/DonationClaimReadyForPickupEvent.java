package com.surplusfood.ordermanagement.order_service.domain.event.donationClaim;

import com.surplusfood.ordermanagement.order_service.domain.model.common.LogisticsId;
import com.surplusfood.ordermanagement.order_service.domain.model.donationclaim.ClaimId;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class DonationClaimReadyForPickupEvent implements DonationClaimDomainEvent{
    ClaimId claimId;
    LogisticsId logisticsId;
    LocalDateTime timestamp;
}