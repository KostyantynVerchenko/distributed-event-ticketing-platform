package com.kostyantynverchenko.payment.dto;

import com.kostyantynverchenko.payment.entity.PaymentCurrency;
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
    private PaymentCurrency currency;

    public CreatePaymentRequest() {

    }

    public CreatePaymentRequest(UUID orderId, BigDecimal amount, PaymentCurrency currency) {
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

    public PaymentCurrency getCurrency() {
        return currency;
    }

    public void setCurrency(PaymentCurrency currency) {
        this.currency = currency;
    }
}
