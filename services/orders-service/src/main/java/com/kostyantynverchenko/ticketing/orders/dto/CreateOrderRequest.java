package com.kostyantynverchenko.ticketing.orders.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateOrderRequest {
    @NotNull
    private UUID eventId;

    @NotNull
    @Min(1)
    private Integer quantity;

    public CreateOrderRequest() {}

    public CreateOrderRequest(UUID eventId, Integer quantity) {
        this.eventId = eventId;
        this.quantity = quantity;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
