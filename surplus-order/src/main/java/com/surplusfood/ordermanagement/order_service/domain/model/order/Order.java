package com.surplusfood.ordermanagement.order_service.domain.model.order;

import com.surplusfood.ordermanagement.order_service.domain.event.order.*;
import com.surplusfood.ordermanagement.order_service.domain.exception.InvalidOrderStatusException;
import com.surplusfood.ordermanagement.order_service.domain.model.common.Address;
import com.surplusfood.ordermanagement.order_service.domain.model.common.ListingId;
import com.surplusfood.ordermanagement.order_service.domain.model.common.LogisticsId;
import com.surplusfood.ordermanagement.order_service.domain.model.common.Quantity;
import com.surplusfood.ordermanagement.order_service.domain.model.common.UserId;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@EqualsAndHashCode(of = "orderId", callSuper = false)
@ToString(exclude = "orderItems")
public class Order extends AbstractAggregateRoot<Order> {
    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "order_id"))
    private OrderId orderId;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "buyer_id", nullable = false))
    })
    private UserId buyerId;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "seller_id", nullable = false))
    })
    private UserId sellerId;

    @Column(name = "order_date",nullable = false)
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Embedded
    private Address shippingAddress;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "total_currency"))
    })
    private Money totalAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "discount_applied_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "discount_applied_currency"))
    })
    private Money discountApplied;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "payment_id"))
    })
    private PaymentId paymentId;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "logistics_id"))
    })
    private LogisticsId logisticsId;

    @Version
    private int version;

    protected Order() {
        // For JPA
    }

    public static Order placeOrder(UserId buyerId, UserId sellerId, Address shippingAddress, 
                                 List<OrderItemData> itemsData, CurrencyCode currency) {
        if (itemsData == null || itemsData.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }

        Order order = new Order();
        order.orderId = OrderId.newInstance();
        order.buyerId = buyerId;
        order.sellerId = sellerId;
        order.shippingAddress = shippingAddress;
        order.orderDate = LocalDateTime.now();
        order.status = OrderStatus.PENDING_PAYMENT;
        order.totalAmount = Money.zero(currency);
        order.discountApplied = Money.zero(currency);

        for (OrderItemData itemData : itemsData) {
            order.addItem(itemData.listingId(), itemData.productName(), 
                         itemData.quantity(), itemData.unitPrice());
        }

        order.registerEvent(new OrderPlacedEvent(
            order.orderId,
            order.buyerId,
            order.sellerId,
            order.totalAmount,
            order.orderDate
        ));

        return order;
    }

    public void addItem(ListingId listingId, String productName, Quantity quantity, Money unitPrice) {
        if (status != OrderStatus.PENDING_PAYMENT) {
            throw new InvalidOrderStatusException(status.name(), "PENDING_PAYMENT");
        }

        OrderItem item = new OrderItem(this, listingId, productName, quantity, unitPrice);
        orderItems.add(item);
        calculateTotal();
    }

    public void removeItem(OrderItemId orderItemId) {
        if (status != OrderStatus.PENDING_PAYMENT) {
            throw new InvalidOrderStatusException(status.name(), "PENDING_PAYMENT");
        }

        orderItems.removeIf(item -> item.getOrderItemId().equals(orderItemId));
        calculateTotal();
    }

    public void calculateTotal() {
        Money subtotal = orderItems.stream()
            .map(OrderItem::getItemSubtotal)
            .reduce(Money.zero(totalAmount.getCurrency()), Money::add);
        
        this.totalAmount = subtotal.subtract(discountApplied);
    }

    public void applyDiscount(Money discountAmount) {
        if (status != OrderStatus.PENDING_PAYMENT) {
            throw new InvalidOrderStatusException(status.name(), "PENDING_PAYMENT");
        }
        if (discountAmount.isGreaterThan(totalAmount)) {
            throw new IllegalArgumentException("Discount cannot be greater than total amount");
        }
        this.discountApplied = discountAmount;
        calculateTotal();
    }

    public void confirmPayment(PaymentId paymentId) {
        if (status != OrderStatus.PENDING_PAYMENT) {
            throw new InvalidOrderStatusException(status.name(), "PENDING_PAYMENT");
        }
        this.paymentId = paymentId;
        this.status = OrderStatus.PENDING_CONFIRMATION;
        registerEvent(new OrderPaymentSuccessfulEvent(orderId, paymentId, LocalDateTime.now()));
    }

    public void confirmOrder() {
        if (status != OrderStatus.PENDING_PAYMENT) {
            throw new InvalidOrderStatusException(status.name(), "PENDING_PAYMENT");
        }
        this.status = OrderStatus.CONFIRMED;
    }

    public void rejectOrder(String reason) {
        if (status != OrderStatus.PENDING_PAYMENT) {
            throw new InvalidOrderStatusException(status.name(), "PENDING_PAYMENT");
        }
        this.status = OrderStatus.CANCELLED;
        registerEvent(new OrderCancelledEvent(orderId, reason, LocalDateTime.now()));
    }

    public void markAsPreparing() {
        if (status != OrderStatus.CONFIRMED) {
            throw new InvalidOrderStatusException(status.name(), "PREPARING");
        }

        this.status = OrderStatus.PREPARING;
    }

    public void markAsReadyForPickup(LogisticsId logisticsId) {
        if (status != OrderStatus.PREPARING) {
            throw new InvalidOrderStatusException(status.name(), "READY_FOR_PICKUP");
        }

        this.logisticsId = logisticsId;
        this.status = OrderStatus.READY_FOR_PICKUP;
    }

    public void markAsShipped(String trackingNumber, LogisticsId logisticsId) {
        if (status != OrderStatus.CONFIRMED && status != OrderStatus.READY_FOR_PICKUP) {
            throw new InvalidOrderStatusException(status.name(), "SHIPPED");
        }
        this.logisticsId = logisticsId;
        this.status = OrderStatus.SHIPPED;
        registerEvent(new OrderShippedEvent(orderId, logisticsId, trackingNumber, LocalDateTime.now()));
    }

    public void markAsDelivered() {
        if (status != OrderStatus.SHIPPED) {
            throw new InvalidOrderStatusException(status.name(), "DELIVERED");
        }

        this.status = OrderStatus.DELIVERED;
        registerEvent(new OrderDeliveredEvent(orderId, LocalDateTime.now()));
    }



    public void cancelOrder(String reason) {
        if (status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED) {
            throw new InvalidOrderStatusException(status.name(), "CANCELLED");
        }

        this.status = OrderStatus.CANCELLED;
        registerEvent(new OrderCancelledEvent(orderId, reason, LocalDateTime.now()));
    }

    public void initiateRefund() {
        if (status != OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusException(status.name(), "REFUNDED");
        }

        this.status = OrderStatus.REFUNDED;
    }

    // Helper record for order creation
    public record OrderItemData(
        ListingId listingId,
        String productName,
        Quantity quantity,
        Money unitPrice
    ) {}

    public static Order rehydrate(
            OrderId orderId,
            UserId buyerId,
            UserId sellerId,
            LocalDateTime orderDate,
            OrderStatus status,
            Address shippingAddress,
            Money totalAmount,
            Money discountApplied,
            PaymentId paymentId,
            LogisticsId logisticsId,
            List<OrderItem> orderItems
    ) {
        Order order = new Order();
        order.orderId = orderId;
        order.buyerId = buyerId;
        order.sellerId = sellerId;
        order.orderDate = orderDate;
        order.status = status;
        order.shippingAddress = shippingAddress;
        order.totalAmount = totalAmount;
        order.discountApplied = discountApplied;
        order.paymentId = paymentId;
        order.logisticsId = logisticsId;
        order.orderItems = orderItems;

// Make sure to link each item back to the order
        orderItems.forEach(item -> item.setOrder(order));
        return order;
    }
}
