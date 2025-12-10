package com.kostyantynverchenko.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kostyantynverchenko.payment.dto.event.PaymentEventMessage;
import com.kostyantynverchenko.payment.dto.event.PaymentEventPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class PaymentEventPublisher {

    @Value("${kafka.payments.topic:payments.events}")
    private String paymentsTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public PaymentEventPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishPaymentEvent(PaymentEventPayload payload, String eventType) {
        try {
            PaymentEventMessage paymentEventMessage = new PaymentEventMessage(
                    UUID.randomUUID(),
                    eventType,
                    "Payment",
                    payload.getPaymentId(),
                    objectMapper.writeValueAsString(payload)
            );

            String json = objectMapper.writeValueAsString(paymentEventMessage);
            kafkaTemplate.send(paymentsTopic, payload.getOrderId().toString(), json).get();
            log.info("Published payment event {} for order {} with status {}", paymentEventMessage.getId(), payload.getOrderId(), payload.getPaymentStatus());
        } catch (Exception e) {
            log.error("Error publishing payment event for order {}: {}", payload.getOrderId(), e.getMessage());
        }
    }
}
