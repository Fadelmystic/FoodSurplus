package com.surplusfood.ordermanagement.order_service.domain.model.common;

import lombok.Data;
import lombok.Value;

@Value
@Data
public class Quantity {
    int value;
    String unit;

    private Quantity(int value, String unit) {
        if (value <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (unit == null || unit.isBlank()) {
            throw new IllegalArgumentException("Unit cannot be null or blank");
        }
        this.value = value;
        this.unit = unit;
    }

    public static Quantity of(int value, String unit) {
        return new Quantity(value, unit);
    }

    public Quantity add(Quantity other) {
        if (!this.unit.equals(other.unit)) {
            throw new IllegalArgumentException("Cannot add quantities with different units");
        }
        return new Quantity(this.value + other.value, this.unit);
    }

    public Quantity subtract(Quantity other) {
        if (!this.unit.equals(other.unit)) {
            throw new IllegalArgumentException("Cannot subtract quantities with different units");
        }
        int result = this.value - other.value;
        if (result < 0) {
            throw new IllegalArgumentException("Result cannot be negative");
        }
        return new Quantity(result, this.unit);
    }
} 