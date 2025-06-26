package com.surplusfood.ordermanagement.order_service.application.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final WebClient webClient;

    public UserService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://user-management").build();
    }

    public Mono<Boolean> isUserEnabled(String userId, String bearerToken) {
        return webClient
                .get()
                .uri("/api/users/{id}/enabled", userId)
                .headers(headers -> headers.setBearerAuth(bearerToken)) // add auth token
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(e -> {
                    // Log or handle errors gracefully
                    return Mono.just(false);
                });
    }
}
