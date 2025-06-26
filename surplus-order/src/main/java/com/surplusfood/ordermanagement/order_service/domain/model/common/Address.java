package com.surplusfood.ordermanagement.order_service.domain.model.common;

import jakarta.persistence.Embeddable;
import lombok.Value;

@Value
@Embeddable
public class Address {
    String street;
    String city;
    String postalCode;
    String country;

    private Address(String street, String city, String postalCode, String country) {
        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException("Street cannot be null or blank");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City cannot be null or blank");
        }
        if (postalCode == null || postalCode.isBlank()) {
            throw new IllegalArgumentException("Postal code cannot be null or blank");
        }
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException("Country cannot be null or blank");
        }
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
    }



    public static Address of(String street, String city, String postalCode, String country) {
        return new Address(street, city, postalCode, country);
    }
}