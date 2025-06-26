package com.surplusfood.ordermanagement.order_service.domain.exception;

public class InvalidDonationClaimStatusException extends RuntimeException {
    public InvalidDonationClaimStatusException(String message) {
        super(message);
    }

    public InvalidDonationClaimStatusException(String currentStatus, String attemptedStatus) {
        super("Invalid status change: cannot transition from " + currentStatus + " to " + attemptedStatus);
    }
}
