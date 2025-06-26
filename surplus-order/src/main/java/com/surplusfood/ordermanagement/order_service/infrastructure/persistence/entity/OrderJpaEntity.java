package com.surplusfood.ordermanagement.order_service.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderJpaEntity {

    @Id
    @Column(name = "order_id")
    private String id;

    @Column(name = "buyer_id", nullable = false)
    private String buyerId;

    @Column(name = "seller_id", nullable = false)
    private String sellerId;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "status", nullable = false)
    private String status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItemJpaEntity> items;

    @Column(name = "shipping_street")
    private String shippingStreet;

    @Column(name = "shipping_city")
    private String shippingCity;

    @Column(name = "shipping_postal_code")
    private String shippingPostalCode;

    @Column(name = "shipping_country")
    private String shippingCountry;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "total_currency")
    private String totalCurrency;

    @Column(name = "discount_applied_amount")
    private BigDecimal discountAppliedAmount;

    @Column(name = "discount_applied_currency")
    private String discountAppliedCurrency;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "logistics_id")
    private String logisticsId;
}