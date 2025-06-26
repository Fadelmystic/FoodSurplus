package com.surplusfood.ordermanagement.order_service.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemJpaEntity {

    @Id
    @Column(name = "order_item_id")
    private String id;

    @Column(name = "listing_id", nullable = false)
    private String listingId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "quantity_value", nullable = false)
    private int quantityValue;

    @Column(name = "quantity_unit", nullable = false)
    private String quantityUnit;

    @Column(name = "unit_price_amount", nullable = false)
    private BigDecimal unitPriceAmount;

    @Column(name = "unit_price_currency", nullable = false)
    private String unitPriceCurrency;

    @Column(name = "item_subtotal_amount", nullable = false)
    private BigDecimal itemSubtotalAmount;

    @Column(name = "item_subtotal_currency", nullable = false)
    private String itemSubtotalCurrency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderJpaEntity order;
}