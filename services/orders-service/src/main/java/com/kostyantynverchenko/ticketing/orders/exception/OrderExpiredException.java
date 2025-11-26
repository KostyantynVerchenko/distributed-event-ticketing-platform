package com.kostyantynverchenko.ticketing.orders.exception;

import java.util.UUID;

public class OrderExpiredException extends RuntimeException {

    private UUID orderId;

    public OrderExpiredException(UUID orderId) {
        super("Order with id " + orderId + " has expired");
        this.orderId = orderId;
    }

    public UUID getOrderId() {
        return orderId;
    }
}
