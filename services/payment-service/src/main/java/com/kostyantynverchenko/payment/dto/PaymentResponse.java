package com.kostyantynverchenko.payment.dto;

import com.kostyantynverchenko.payment.entity.Payment;
import com.kostyantynverchenko.payment.entity.PaymentCurrency;
import com.kostyantynverchenko.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentResponse {
    private UUID id;
    private UUID orderId;
    private BigDecimal amount;
    private PaymentCurrency currency;
    private PaymentStatus paymentStatus;
    private String failureReason;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public PaymentResponse() {
    }

    public PaymentResponse(Payment payment) {
        this.id = payment.getId();
        this.orderId = payment.getOrderId();
        this.amount = payment.getAmount();
        this.currency = payment.getCurrency();
        this.paymentStatus = payment.getStatus();
        this.failureReason = payment.getFailureReason();
        this.createdAt = payment.getCreatedAt();
        this.updatedAt = payment.getUpdatedAt();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
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
}
