package com.kostyantynverchenko.ticketing.orders.dto;

import com.kostyantynverchenko.ticketing.orders.entity.Order;
import com.kostyantynverchenko.ticketing.orders.entity.OrderCurrency;
import com.kostyantynverchenko.ticketing.orders.entity.OrderItem;
import com.kostyantynverchenko.ticketing.orders.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderResponse {
    private UUID orderId;
    private UUID userId;
    private OrderStatus orderStatus;
    private BigDecimal totalAmount;
    private OrderCurrency orderCurrency;
    private LocalDateTime reservedUntil;
    private UUID paymentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<OrderItemResponse> orderItems;

    public OrderResponse() {}

    public OrderResponse(Order order) {
        this.orderId = order.getId();
        this.userId = order.getUserId();
        this.orderStatus = order.getOrderStatus();
        this.totalAmount = order.getTotalAmount();
        this.orderCurrency = order.getOrderCurrency();
        this.reservedUntil = order.getReservedUntil();
        this.paymentId = order.getPaymentId();
        this.createdAt = order.getCreatedAt();
        this.updatedAt = order.getUpdatedAt();
        this.orderItems = order.getOrderItems().stream()
                .map(OrderItemResponse::new)
                .toList();
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

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderCurrency getOrderCurrency() {
        return orderCurrency;
    }

    public void setOrderCurrency(OrderCurrency orderCurrency) {
        this.orderCurrency = orderCurrency;
    }

    public LocalDateTime getReservedUntil() {
        return reservedUntil;
    }

    public void setReservedUntil(LocalDateTime reservedUntil) {
        this.reservedUntil = reservedUntil;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<OrderItemResponse> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemResponse> orderItems) {
        this.orderItems = orderItems;
    }
}
