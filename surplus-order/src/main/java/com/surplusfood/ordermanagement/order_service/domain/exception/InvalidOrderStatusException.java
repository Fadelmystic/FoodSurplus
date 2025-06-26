package com.surplusfood.ordermanagement.order_service.domain.exception;

/**
 * Exception thrown when an invalid status transition is attempted on an order.
 */
public class InvalidOrderStatusException extends RuntimeException {

    public InvalidOrderStatusException(String message) {
        super(message);
    }

    public InvalidOrderStatusException(String currentStatus, String attemptedStatus) {
        super("Invalid status change: cannot transition from " + currentStatus + " to " + attemptedStatus);
    }
}
