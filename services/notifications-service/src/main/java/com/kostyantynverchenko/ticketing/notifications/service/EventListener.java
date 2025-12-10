package com.kostyantynverchenko.ticketing.notifications.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kostyantynverchenko.ticketing.notifications.dto.*;
import com.kostyantynverchenko.ticketing.notifications.entity.NotificationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventListener {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public EventListener(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.orders.topic:orders.events}", groupId = "${spring.kafka.consumer.group-id:notifications-service}")
    public void handleOrderEvent(String message) {
        try {
            OrderEventMessage orderEventMessage = objectMapper.readValue(message, OrderEventMessage.class);
            log.info("Received order event {} from topic: {}", orderEventMessage.getId(), orderEventMessage.getEventType());

            OrderEventPayload orderEventPayload = objectMapper.readValue(orderEventMessage.getPayload(), OrderEventPayload.class);

            String eventType = orderEventMessage.getEventType();
            String text;

            switch (eventType) {
                case "ORDER_CREATED" -> text =
                        "Order with id: " + orderEventPayload.getOrderId() + " was created";
                case "ORDER_PAID" -> text =
                        "Order with id: " + orderEventPayload.getOrderId() + " was paid successfully";
                case "ORDER_CANCELLED" -> text =
                        "Order with id: " + orderEventPayload.getOrderId() + " was cancelled";
                case "ORDER_EXPIRED" -> text =
                        "Order with id: " + orderEventPayload.getOrderId() + " has expired";
                case "ORDER_FAILED" -> text =
                        "Order with id: " + orderEventPayload.getOrderId() + " has failed";
                default -> {
                    return;
                }
            }

            CreateNotificationRequest request = new CreateNotificationRequest(orderEventPayload.getOrderId(), orderEventPayload.getUserId(), eventType, text, NotificationStatus.NEW);

            notificationService.create(request);
        } catch (Exception e) {
            log.error("Error while processing order event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "${kafka.payments.topic:payments.events}", groupId = "${spring.kafka.consumer.group-id:notifications-service}")
    public void handlePaymentEvent(String message) {
        try {
            PaymentEventMessage paymentEventMessage = objectMapper.readValue(message, PaymentEventMessage.class);
            log.info("Received payment event {} from topic: {}", paymentEventMessage.getId(), paymentEventMessage.getEventType());

            PaymentEventPayload paymentEventPayload = objectMapper.readValue(paymentEventMessage.getPayload(), PaymentEventPayload.class);

            String eventType = paymentEventMessage.getEventType();
            String text;

            switch (eventType) {
                case "PAYMENT_SUCCESS" -> text =
                        "Payment with id: " + paymentEventPayload.getPaymentId() + " was successful";
                case "PAYMENT_FAILED" -> text =
                        "Payment with id: " + paymentEventPayload.getPaymentId() + " has failed";
                default -> {
                    return;
                }
            }

            CreateNotificationRequest request = new CreateNotificationRequest(paymentEventPayload.getOrderId(), null,  eventType, text, NotificationStatus.NEW);

            notificationService.create(request);
        } catch (Exception e) {
            log.error("Error while processing payment event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "${kafka.events.topic:events.events}", groupId = "${spring.kafka.consumer.group-id:notifications-service}")
    public void handleEventsEvent(String message) {
        try {
            EventMessage eventMessage = objectMapper.readValue(message, EventMessage.class);
            log.info("Received event {} from topic: {}", eventMessage.getId(), eventMessage.getEventType());

            EventPayload eventPayload = objectMapper.readValue(eventMessage.getPayload(), EventPayload.class);

            String eventType = eventMessage.getEventType();
            String text;

            switch (eventType) {
                case "EVENT_CANCELLED" -> text =
                        "Event \"" + eventPayload.getTitle() + "\" on " + eventPayload.getDate() + " was cancelled";
                case "EVENT_EXPIRED" -> text =
                        "Event \"" + eventPayload.getTitle() + "\" is no longer available for booking";
                case "EVENT_DELETED" -> text =
                        "Event \"" + eventPayload.getTitle() + "\" was removed";
                default ->  {
                    return;
                }
            }

            CreateNotificationRequest request = new CreateNotificationRequest(
                    null, null, eventType, text, NotificationStatus.NEW
            );

            notificationService.create(request);
        } catch (Exception e) {
            log.error("Error while processing payment event: {}", e.getMessage());
        }
    }
}
