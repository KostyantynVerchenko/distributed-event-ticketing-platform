package com.kostyantynverchenko.ticketing.orders.exception;

import com.kostyantynverchenko.ticketing.orders.entity.OrderStatus;

import java.util.UUID;

public class InvalidOrderStateException extends RuntimeException {

    private UUID orderId;
    private OrderStatus orderStatus;

    public InvalidOrderStateException(UUID orderId, OrderStatus orderStatus) {
        super("Cannot change order status for orderId: " + orderId + ", orderStatus: " + orderStatus);
        this.orderId = orderId;
        this.orderStatus = orderStatus;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public UUID getOrderId() {
        return orderId;
    }
}
