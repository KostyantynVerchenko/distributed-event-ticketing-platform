package com.kostyantynverchenko.ticketing.orders.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class EventPayload {
    private UUID orderId;
    private UUID userId;
    private String status;
    private BigDecimal totalAmount;

    public EventPayload() {

    }

    public EventPayload(UUID orderId, UUID userId, String status, BigDecimal totalAmount) {
        this.orderId = orderId;
        this.userId = userId;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
