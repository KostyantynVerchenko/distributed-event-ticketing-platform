package com.kostyantynverchenko.ticketing.orders.client.payment;

import com.kostyantynverchenko.ticketing.orders.entity.OrderCurrency;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentServiceClient {

    private final RestClient restClient;

    public PaymentServiceClient(@Qualifier("paymentRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentResponse createPaymentByOrderId(UUID orderId, BigDecimal amount, OrderCurrency currency) {
        try {
            return restClient.post().uri("/api/payments").body(new CreatePaymentRequest(orderId, amount, currency)).retrieve().body(PaymentResponse.class);
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("Payment not found");
            }
            throw new RuntimeException("Failed to call payment service", ex);
        }
    }
}
