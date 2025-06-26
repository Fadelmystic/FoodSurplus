package com.surplusfood.ordermanagement.order_service.infrastructure.clients;

import org.springframework.stereotype.Component;

@Component
public class MockListingServiceClient implements ListingServiceClient {
    @Override
    public boolean isAvailable(String listingId, int quantity) {
        return true; // Simulate always available
    }
}
