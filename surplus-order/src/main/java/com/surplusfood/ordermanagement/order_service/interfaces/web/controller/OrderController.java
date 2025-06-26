package com.surplusfood.ordermanagement.order_service.interfaces.web.controller;

import com.surplusfood.ordermanagement.order_service.application.command.order.*;
import com.surplusfood.ordermanagement.order_service.application.dto.OrderDetailsDto;
import com.surplusfood.ordermanagement.order_service.application.dto.OrderSummaryDto;
import com.surplusfood.ordermanagement.order_service.application.query.FindOrderByIdQuery;
import com.surplusfood.ordermanagement.order_service.application.query.FindOrdersByUserQuery;
import com.surplusfood.ordermanagement.order_service.application.service.JwtService;
import com.surplusfood.ordermanagement.order_service.application.service.OrderApplicationService;
import com.surplusfood.ordermanagement.order_service.domain.model.common.Address;
import com.surplusfood.ordermanagement.order_service.domain.model.common.UserId;
import com.surplusfood.ordermanagement.order_service.domain.model.order.*;
import com.surplusfood.ordermanagement.order_service.interfaces.web.dto.order.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderApplicationService orderService;
    private final JwtService jwtService;
    @PostMapping
    public ResponseEntity<Void> placeOrder(
            @RequestBody PlaceOrderRequest request,
            @RequestHeader("Authorization") String authorizationHeader) {

        // ✅ Extract token (strip "Bearer ")
        String token = authorizationHeader.replace("Bearer ", "");

        // ✅ Extract user ID from JWT
        String buyerId = jwtService.extractUserId(token); // You must implement this

        var items = request.items().stream()
                .map(i -> new PlaceOrderCommand.OrderItemData(
                        i.listingId(),
                        i.productName(),
                        i.quantity(),
                        BigDecimal.valueOf(i.unitPriceAmount())
                ))
                .collect(Collectors.toList());

        var command = new PlaceOrderCommand(
                UserId.from(buyerId),                                // ✅ Use extracted ID
                UserId.from(request.sellerId()),                     // ✅ Still from request
                Address.of(
                        request.shippingAddress().street(),
                        request.shippingAddress().city(),
                        request.shippingAddress().postalCode(),
                        request.shippingAddress().country()
                ),
                items,
                CurrencyCode.valueOf(request.currency())
        );

        orderService.handle(command);
        return ResponseEntity.ok().build();
    }



    @PostMapping("/accept")
    public ResponseEntity<Void> acceptOrder(@RequestBody AcceptOrderRequest request) {
        var command = new AcceptOrderCommand(
                OrderId.from(request.orderId())
        );
        orderService.handle(command);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reject")
    public ResponseEntity<Void> rejectOrder(@RequestBody RejectOrderRequest request) {
        orderService.handle(new RejectOrderCommand(
                OrderId.from(request.orderId()),
                request.reason())
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancel")
    public ResponseEntity<Void> cancelOrder(@RequestBody CancelOrderRequest request) {
        orderService.handle(new CancelOrderCommand(
                OrderId.from(request.orderId()),
                request.reason())
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/receipt")
    public ResponseEntity<Void> confirmReceipt(@RequestBody ConfirmOrderReceiptRequest request) {
        orderService.handle(new ConfirmOrderReceiptCommand(
                OrderId.from(request.orderId()))
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailsDto> getOrderById(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.handle(new FindOrderByIdQuery(
                OrderId.from(orderId)))
        );
    }

    @GetMapping
    public ResponseEntity<List<OrderSummaryDto>> getOrdersByUser(
            @RequestParam String userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        OrderStatus orderStatus = (status != null) ? OrderStatus.valueOf(status) : null;
        return ResponseEntity.ok(orderService.handle(
                new FindOrdersByUserQuery(UserId.from(userId), orderStatus, page, size)));
    }
}
