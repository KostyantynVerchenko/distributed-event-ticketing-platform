package com.kostyantynverchenko.payment.exception;

import java.util.UUID;

public class PaymentNotFoundException extends RuntimeException {

    private UUID paymentId;

    public PaymentNotFoundException(UUID paymentId) {
        super("Payment with id: " + paymentId + " not found");
        this.paymentId = paymentId;
    }

    public UUID getPaymentId() {
        return paymentId;
    }
}
