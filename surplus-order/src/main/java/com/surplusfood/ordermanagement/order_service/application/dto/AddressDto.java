package com.surplusfood.ordermanagement.order_service.application.dto;

public record AddressDto(
        String street,
        String city,
        String postalCode,
        String country
) {}
