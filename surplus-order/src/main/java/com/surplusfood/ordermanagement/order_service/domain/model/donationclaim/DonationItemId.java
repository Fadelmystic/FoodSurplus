package com.surplusfood.ordermanagement.order_service.domain.model.donationclaim;

import jakarta.persistence.Embeddable;
import lombok.Value;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Value
public class DonationItemId implements Serializable {
    String value;

    protected DonationItemId() {
        this.value = null;
    }

    private DonationItemId(String value) {
        this.value = value;
    }

    public static DonationItemId of(String value) {
        return new DonationItemId(value);
    }

    public static DonationItemId random() {
        return new DonationItemId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }

    public static DonationItemId from(String value) {
        return new DonationItemId(value);
    }

}

