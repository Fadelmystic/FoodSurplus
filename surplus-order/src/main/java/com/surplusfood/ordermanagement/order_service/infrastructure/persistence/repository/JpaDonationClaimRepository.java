package com.surplusfood.ordermanagement.order_service.infrastructure.persistence.repository;

import com.surplusfood.ordermanagement.order_service.domain.model.common.UserId;
import com.surplusfood.ordermanagement.order_service.domain.model.donationclaim.ClaimId;
import com.surplusfood.ordermanagement.order_service.domain.model.donationclaim.ClaimStatus;
import com.surplusfood.ordermanagement.order_service.domain.model.donationclaim.DonationClaim;
import com.surplusfood.ordermanagement.order_service.domain.repository.DonationClaimRepository;
import com.surplusfood.ordermanagement.order_service.infrastructure.persistence.mapper.DonationClaimPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaDonationClaimRepository implements DonationClaimRepository {

    private final SpringDataDonationClaimRepository springDataRepo;
    private final DonationClaimPersistenceMapper mapper;

    @Override
    public void save(DonationClaim donationClaim) {
        springDataRepo.save(mapper.toJpaEntity(donationClaim));
    }

    @Override
    public Optional<DonationClaim> findById(ClaimId claimId) {
        return springDataRepo.findById(ClaimId.newInstance().getValue())
                .map(mapper::toDomainEntity);
    }

    @Override
    public List<DonationClaim> findByNgoIdAndStatus(UserId ngoId, ClaimStatus status, int page, int size) {
        var pageResult = springDataRepo.findByNgoIdAndStatus(
                ngoId.getValue(), status.name(), Pageable.ofSize(size).withPage(page));
        return pageResult.stream()
                .map(mapper::toDomainEntity)
                .toList();
    }

}
