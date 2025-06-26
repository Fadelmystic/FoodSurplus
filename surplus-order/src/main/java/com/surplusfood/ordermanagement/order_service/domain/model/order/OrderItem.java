package com.surplusfood.ordermanagement.order_service.domain.model.order;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

import com.surplusfood.ordermanagement.order_service.domain.model.common.ListingId;
import com.surplusfood.ordermanagement.order_service.domain.model.common.Quantity;

@Entity
@Table(name = "order_items")
@Getter
@EqualsAndHashCode(of = "orderItemId")
@ToString(exclude = "order")
public class OrderItem {
    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "order_item_id"))
    private OrderItemId orderItemId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "listing_id", nullable = false))
    private ListingId listingId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "quantity", nullable = false))
    private Quantity quantity;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "unit_price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "unit_price_currency"))
    })
    private Money unitPrice;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "item_subtotal_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "item_subtotal_currency"))
    })
    private Money itemSubtotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    protected OrderItem() {
        // For JPA
    }

    public OrderItem(Order order, ListingId listingId, String productName, Quantity quantity, Money unitPrice) {
        this.orderItemId = OrderItemId.newInstance();
        this.order = order;
        this.listingId = listingId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        calculateSubtotal();
    }

    public void calculateSubtotal() {
        this.itemSubtotal = unitPrice.multiply(BigDecimal.valueOf(quantity.getValue()));
    }

    public void updateQuantity(Quantity newQuantity) {
        this.quantity = newQuantity;
        calculateSubtotal();
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public static OrderItem rehydrate(
            OrderItemId id,
            ListingId listingId,
            String productName,
            Quantity quantity,
            Money unitPrice,
            Money itemSubtotal
    ) {
        OrderItem item = new OrderItem();
        item.orderItemId = id;
        item.listingId = listingId;
        item.productName = productName;
        item.quantity = quantity;
        item.unitPrice = unitPrice;
        item.itemSubtotal = itemSubtotal;
        return item;
    }


} 