package com.kostyantynverchenko.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kostyantynverchenko.payment.dto.CreatePaymentRequest;
import com.kostyantynverchenko.payment.dto.OrderEventMessage;
import com.kostyantynverchenko.payment.dto.OrderEventPayload;
import com.kostyantynverchenko.payment.entity.PaymentCurrency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderEventListener {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    public OrderEventListener(PaymentService paymentService, ObjectMapper objectMapper) {
        this.paymentService = paymentService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.orders.topic:orders.events}", groupId = "${spring.kafka.consumer.group-id:payment-service}")
    public void handleOrderEvent(String message) {
        try {
            OrderEventMessage orderEventMessage = objectMapper.readValue(message, OrderEventMessage.class);
            log.info("Received order event {} from topic: {}", orderEventMessage.getId(), orderEventMessage.getEventType());

            if (!"ORDER_CREATED".equals(orderEventMessage.getEventType())) {
                return;
            }

            OrderEventPayload orderEventPayload = objectMapper.readValue(orderEventMessage.getPayload(), OrderEventPayload.class);

            CreatePaymentRequest createPaymentRequest = new CreatePaymentRequest(
                    orderEventPayload.getOrderId(),
                    orderEventPayload.getTotalAmount(),
                    PaymentCurrency.valueOf(orderEventPayload.getOrderCurrency())
            );

            paymentService.createAndProcessPayment(createPaymentRequest);
        } catch (Exception e) {
            log.error("Error while processing order event: {}", e.getMessage());
        }
    }
}
