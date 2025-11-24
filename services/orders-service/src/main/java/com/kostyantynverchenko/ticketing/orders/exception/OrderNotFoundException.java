package com.kostyantynverchenko.ticketing.orders.exception;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException {

    private final UUID orderId;

    public OrderNotFoundException(UUID orderId) {
        super("Order with id " + orderId + " not found");
        this.orderId = orderId;
    }

    public UUID getOrderId() {
        return orderId;
    }
}
