package com.kostyantynverchenko.ticketing.orders.client.payment;

import com.kostyantynverchenko.ticketing.orders.entity.OrderCurrency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class CreatePaymentRequest {

    @NotNull
    private UUID orderId;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotNull
    private OrderCurrency currency;

    public CreatePaymentRequest() {

    }

    public CreatePaymentRequest(UUID orderId, BigDecimal amount, OrderCurrency currency) {
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public OrderCurrency getCurrency() {
        return currency;
    }

    public void setCurrency(OrderCurrency currency) {
        this.currency = currency;
    }
}
