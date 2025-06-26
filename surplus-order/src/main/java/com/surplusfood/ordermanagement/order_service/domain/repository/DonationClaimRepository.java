package com.surplusfood.ordermanagement.order_service.domain.repository;

import com.surplusfood.ordermanagement.order_service.domain.model.common.UserId;
import com.surplusfood.ordermanagement.order_service.domain.model.donationclaim.ClaimId;
import com.surplusfood.ordermanagement.order_service.domain.model.donationclaim.ClaimStatus;
import com.surplusfood.ordermanagement.order_service.domain.model.donationclaim.DonationClaim;


import java.util.List;
import java.util.Optional;

public interface DonationClaimRepository {
    void save(DonationClaim donationClaim);
    List<DonationClaim> findByNgoIdAndStatus(UserId ngoId, ClaimStatus status, int page, int size);
    Optional<DonationClaim> findById(ClaimId claimId);

}
