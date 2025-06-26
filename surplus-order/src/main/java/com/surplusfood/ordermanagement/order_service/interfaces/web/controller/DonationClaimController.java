package com.surplusfood.ordermanagement.order_service.interfaces.web.controller;

import com.surplusfood.ordermanagement.order_service.application.command.donationClaim.CancelClaimCommand;
import com.surplusfood.ordermanagement.order_service.application.command.donationClaim.ConfirmDonationReceiptCommand;
import com.surplusfood.ordermanagement.order_service.application.command.donationClaim.PlaceClaimCommand;
import com.surplusfood.ordermanagement.order_service.application.dto.ClaimDetailsDto;
import com.surplusfood.ordermanagement.order_service.application.query.FindClaimByIdQuery;
import com.surplusfood.ordermanagement.order_service.application.query.FindDonationClaimsByUserQuery;
import com.surplusfood.ordermanagement.order_service.application.service.DonationClaimApplicationService;
import com.surplusfood.ordermanagement.order_service.domain.model.common.Address;
import com.surplusfood.ordermanagement.order_service.domain.model.common.UserId;
import com.surplusfood.ordermanagement.order_service.domain.model.donationclaim.ClaimId;
import com.surplusfood.ordermanagement.order_service.domain.model.donationclaim.ClaimStatus;
import com.surplusfood.ordermanagement.order_service.interfaces.web.dto.donationClaim.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class DonationClaimController {

    private final DonationClaimApplicationService claimService;

    @PostMapping
    public ResponseEntity<Void> placeClaim(@RequestBody PlaceClaimRequest request) {
        var addressDto = request.pickupAddress();
        var pickupAddress = Address.of(
                addressDto.street(),
                addressDto.city(),
                addressDto.postalCode(),
                addressDto.country()
        );

        var itemInputs = request.items().stream()
                .map(i -> new PlaceClaimCommand.ClaimedItemData(
                        i.listingId(),
                        i.productName(),
                        i.quantity()
                ))
                .collect(Collectors.toList());

        var command = new PlaceClaimCommand(
                UserId.from("user-123"), // TODO: Replace with actual user context
                UserId.from("seller-456"), // TODO: Resolve based on listing
                pickupAddress,
                request.pickupInstructions(),
                itemInputs
        );

        claimService.handle(command);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/receipt")
    public ResponseEntity<Void> confirmReceipt(@RequestBody ConfirmDonationReceiptRequest request) {
        var command = new ConfirmDonationReceiptCommand(
                ClaimId.from(request.claimId())
        );
        claimService.handle(command);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/cancel")
    public ResponseEntity<Void> cancelClaim(@RequestBody CancelClaimRequest request) {
        var command = new CancelClaimCommand(
                ClaimId.from(request.claimId()), // Fix the type
                request.reason()
        );
        claimService.handle(command); // Ensure this method exists
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{claimId}")
    public ResponseEntity<ClaimDetailsDto> getClaimById(@PathVariable String claimId) {
        var dto = claimService.handle(new FindClaimByIdQuery(
                ClaimId.from(claimId))
        );
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<ClaimDetailsDto>> getClaimsByUser(@RequestParam String userId,
                                                                 @RequestParam(required = false) String status,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        var claimStatus = (status != null && !status.isBlank()) ? ClaimStatus.valueOf(status.toUpperCase()) : null;

        var dtoList = claimService.handle(new FindDonationClaimsByUserQuery(
                UserId.from(userId),
                claimStatus,
                page,
                size)
        );

        return ResponseEntity.ok(dtoList);
    }
}
