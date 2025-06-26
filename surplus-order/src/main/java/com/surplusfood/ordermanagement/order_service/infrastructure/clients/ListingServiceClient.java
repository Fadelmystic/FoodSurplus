package com.surplusfood.ordermanagement.order_service.infrastructure.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "listing-service", url = "${listing-service.url}")
public interface ListingServiceClient {
    @GetMapping("/api/listings/{id}/availability")
    boolean isAvailable(@PathVariable String id, @RequestParam int quantity);
}