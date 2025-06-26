package com.foodsurplus.listing_service.infrastructure.client;



import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "user-management",
        configuration = com.foodsurplus.listing_service.config.FeignClientConfig.class
)
public interface UserClient {
    @GetMapping("/api/users/{id}/enabled")
    Boolean isUserEnabled(@PathVariable("id") String userId);
}


