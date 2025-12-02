package com.kostyantynverchenko.payment.dto.event;

import com.kostyantynverchenko.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentEventPayload {

    private UUID paymentId;
    private UUID orderId;
    private BigDecimal amount;
    private PaymentStatus paymentStatus;
    private String failureReason;

    public PaymentEventPayload() {

    }

    public PaymentEventPayload(UUID paymentId, UUID orderId, BigDecimal amount, PaymentStatus paymentStatus, String failureReason) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.failureReason = failureReason;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
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
}
