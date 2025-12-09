package com.kostyantynverchenko.ticketing.notifications.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kostyantynverchenko.ticketing.notifications.dto.CreateNotificationRequest;
import com.kostyantynverchenko.ticketing.notifications.dto.OrderEventMessage;
import com.kostyantynverchenko.ticketing.notifications.dto.OrderEventPayload;
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
    public void handleOrderEvent(String message){
        try {
            OrderEventMessage orderEventMessage = objectMapper.readValue(message, OrderEventMessage.class);
            log.info("Received order event {} from topic: {}", orderEventMessage.getId(), orderEventMessage.getEventType());

            if (!"ORDER_CREATED".equals(orderEventMessage.getEventType())) {
                return;
            }

            OrderEventPayload orderEventPayload = objectMapper.readValue(orderEventMessage.getPayload(), OrderEventPayload.class);

            CreateNotificationRequest request = new CreateNotificationRequest(orderEventPayload.getOrderId(), orderEventPayload.getUserId(), "ORDER_CREATED", "Order with id: " + orderEventPayload.getOrderId() + "was created", NotificationStatus.NEW);

            notificationService.create(request);
        } catch (Exception e) {
            log.error("Error while processing order event: {}", e.getMessage());
        }
    }
}
