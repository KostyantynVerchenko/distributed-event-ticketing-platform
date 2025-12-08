package com.kostyantynverchenko.ticketing.orders.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kostyantynverchenko.ticketing.orders.dto.CreateOrderRequest;
import com.kostyantynverchenko.ticketing.orders.dto.OrderResponse;
import com.kostyantynverchenko.ticketing.orders.entity.Order;
import com.kostyantynverchenko.ticketing.orders.service.JwtService;
import com.kostyantynverchenko.ticketing.orders.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;
    private final JwtService jwtService;

    public OrderController(OrderService orderService, JwtService jwtService) {
        this.orderService = orderService;
        this.jwtService = jwtService;
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders() {
        log.info("Request to get all Orders");

        List<Order> ordersList = orderService.getAllOrders();
        List<OrderResponse> orders = ordersList.stream().map(OrderResponse::new).toList();

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable UUID id) {
        log.info("Request to get Order with id {}", id);

        Order order = orderService.getOrderById(id);
        OrderResponse orderResponse = new OrderResponse(order);

        return ResponseEntity.ok(orderResponse);
    }

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@RequestHeader("Authorization") String authHeader, @RequestBody CreateOrderRequest dto) {
        log.info("Request to create Order");

        String token = authHeader.substring(7);
        UUID userId = jwtService.extractUserId(token);
        Order order = orderService.createOrder(dto, userId);
        OrderResponse orderResponse = new OrderResponse(order);

        return ResponseEntity.ok(orderResponse);
    }

    @PostMapping("orders/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable UUID id) {
        log.info("Request to cancel Order with id {}", id);

        orderService.cancelPayment(id);
        OrderResponse orderResponse = new OrderResponse(orderService.getOrderById(id));

        return ResponseEntity.ok(orderResponse);
    }
}
