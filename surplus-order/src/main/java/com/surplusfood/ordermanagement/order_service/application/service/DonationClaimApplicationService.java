package com.surplusfood.ordermanagement.order_service.application.service;



import com.surplusfood.ordermanagement.order_service.application.command.donationClaim.CancelClaimCommand;
import com.surplusfood.ordermanagement.order_service.application.command.donationClaim.ConfirmDonationReceiptCommand;
import com.surplusfood.ordermanagement.order_service.application.command.donationClaim.PlaceClaimCommand;
import com.surplusfood.ordermanagement.order_service.application.dto.ClaimDetailsDto;
import com.surplusfood.ordermanagement.order_service.application.query.FindClaimByIdQuery;
import com.surplusfood.ordermanagement.order_service.application.query.FindDonationClaimsByUserQuery;
import com.surplusfood.ordermanagement.order_service.domain.exception.DonationClaimNotFoundException;
import com.surplusfood.ordermanagement.order_service.domain.model.common.ListingId;
import com.surplusfood.ordermanagement.order_service.domain.model.common.Quantity;
import com.surplusfood.ordermanagement.order_service.domain.model.donationclaim.DonationClaim;
import com.surplusfood.ordermanagement.order_service.domain.repository.DonationClaimRepository;
import com.surplusfood.ordermanagement.order_service.infrastructure.clients.ListingServiceClient;
import com.surplusfood.ordermanagement.order_service.infrastructure.persistence.mapper.DonationClaimPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DonationClaimApplicationService {

    private final DonationClaimRepository donationClaimRepository;
    private final DonationClaimPersistenceMapper donationClaimMapper;
    private final ListingServiceClient listingServiceClient;


    @Transactional
    public void handle(PlaceClaimCommand command) {
        // Validate item availability
        command.items().forEach(item -> {
            boolean available = listingServiceClient.isAvailable(item.listingId(), item.quantity());
            if (!available) {
                throw new IllegalStateException("Item " + item.listingId() + " is not available for claim.");
            }
        });

        // Prepare domain-level item input
        var itemInputs = command.items().stream()
                .map(item -> new DonationClaim.DonationItemInput(
                        ListingId.from(item.listingId()),
                        item.productName(),
                        Quantity.of(item.quantity(), "pcs") // Use actual unit if available
                ))
                .toList();

        // Create claim
        var claim = DonationClaim.create(
                command.ngoId(),
                command.sellerId(),
                command.pickupAddress(),
                command.pickupInstructions(),
                itemInputs
        );

        donationClaimRepository.save(claim);
    }

    @Transactional
    public void handle(CancelClaimCommand command) {
        var claim = donationClaimRepository.findById(command.claimId())
                .orElseThrow(() -> new DonationClaimNotFoundException(command.claimId()));
        claim.cancelClaim(command.reason());
        donationClaimRepository.save(claim);
    }

    @Transactional
    public void handle(ConfirmDonationReceiptCommand command) {
        var donation = donationClaimRepository.findById(command.claimId())
                .orElseThrow(() -> new DonationClaimNotFoundException(command.claimId()));
        donation.markAsCollected();
        donationClaimRepository.save(donation);
    }

    @Transactional(readOnly = true)
    public List<ClaimDetailsDto> handle(FindDonationClaimsByUserQuery query) {
        var claims = donationClaimRepository.findByNgoIdAndStatus(
                query.userId(), query.status(), query.page(), query.size()
        );
        return claims.stream()
                .map(donationClaimMapper::toClaimDetailsDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClaimDetailsDto handle(FindClaimByIdQuery query) {
        var claim = donationClaimRepository.findById(query.claimId())
                .orElseThrow(() -> new DonationClaimNotFoundException(query.claimId()));
        return donationClaimMapper.toClaimDetailsDto(claim);
    }


}
