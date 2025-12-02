package com.kostyantynverchenko.ticketing.orders.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kostyantynverchenko.ticketing.orders.dto.payment.PaymentEventMessage;
import com.kostyantynverchenko.ticketing.orders.dto.payment.PaymentEventPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentEventListener {

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    public PaymentEventListener(OrderService orderService, ObjectMapper objectMapper) {
        this.orderService = orderService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.payments.topic:payments.events}", groupId = "${spring.kafka.consumer.group-id:orders-service}")
    public void handlePaymentEvent(String message) {
        try {
            PaymentEventMessage paymentEventMessage = objectMapper.readValue(message, PaymentEventMessage.class);
            log.info("Received payment event {} from topic", paymentEventMessage);

            PaymentEventPayload paymentEventPayload = objectMapper.readValue(paymentEventMessage.getPayload(), PaymentEventPayload.class);
            orderService.processPaymentEvent(paymentEventPayload);
        } catch (Exception e) {
            log.error("Error while processing payment event {}", e.getMessage());
        }
    }
}
