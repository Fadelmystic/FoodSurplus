package com.surplusfood.ordermanagement.order_service;

import com.surplusfood.ordermanagement.order_service.application.command.order.PlaceOrderCommand;
import com.surplusfood.ordermanagement.order_service.application.command.order.CancelOrderCommand;
import com.surplusfood.ordermanagement.order_service.application.service.OrderApplicationService;
import com.surplusfood.ordermanagement.order_service.domain.model.common.Address;
import com.surplusfood.ordermanagement.order_service.domain.model.common.UserId;
import com.surplusfood.ordermanagement.order_service.domain.model.order.CurrencyCode;
import com.surplusfood.ordermanagement.order_service.domain.model.order.OrderId;
import com.surplusfood.ordermanagement.order_service.domain.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
class OrderServiceApplicationTests {
/*
	@Test
	void contextLoads() {
	}
*/

    @Autowired
    private OrderApplicationService orderService;
    @Autowired
    private OrderRepository orderRepository;

    @Test
    void testOrderPlacement() {
        UserId buyerId = UserId.newInstance();
        UserId sellerId = UserId.newInstance();
        Address address = Address.of("1 rue de la Paix", "Paris", "75000", "France");
        PlaceOrderCommand.OrderItemData item = new PlaceOrderCommand.OrderItemData(
                "listing-123", "Produit Test", 2, new BigDecimal("10.00")
        );
        PlaceOrderCommand command = new PlaceOrderCommand(
                buyerId,
                sellerId,
                address,
                List.of(item),
                CurrencyCode.EUR
        );

        // Act
        orderService.handle(command);

        // Assert: on vérifie qu'une commande existe pour ce buyerId
        var orders = orderRepository.findByUserIdAndStatus(buyerId, null, 0, 10);
        assertFalse(orders.isEmpty(), "La commande doit être persistée");
        assertEquals(buyerId, orders.get(0).getBuyerId());
        assertEquals("Produit Test", orders.get(0).getOrderItems().get(0).getProductName());
    }

    @Test
    void testOrderCancellation() {
        // Préparation : création d'une commande
        UserId buyerId = UserId.newInstance();
        UserId sellerId = UserId.newInstance();
        Address address = Address.of("2 avenue des Champs", "Paris", "75008", "France");
        PlaceOrderCommand.OrderItemData item = new PlaceOrderCommand.OrderItemData(
                "listing-456", "Produit Annulable", 1, new BigDecimal("20.00")
        );
        PlaceOrderCommand command = new PlaceOrderCommand(
                buyerId,
                sellerId,
                address,
                List.of(item),
                CurrencyCode.EUR
        );
        orderService.handle(command);
        var orders = orderRepository.findByUserIdAndStatus(buyerId, null, 0, 10);
        assertFalse(orders.isEmpty(), "La commande doit être créée avant annulation");
        var order = orders.get(0);

        // Act: annulation
        CancelOrderCommand cancelCommand = new CancelOrderCommand(order.getOrderId(), "Test d'annulation");
        orderService.handle(cancelCommand);

        // Assert: statut annulé
        var cancelledOrder = orderRepository.findById(order.getOrderId()).orElseThrow();
        assertEquals("CANCELLED", cancelledOrder.getStatus().name(), "La commande doit être annulée");
    }
}
