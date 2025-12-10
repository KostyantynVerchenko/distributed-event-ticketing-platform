package com.kostyantynverchenko.ticketing.events.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kostyantynverchenko.ticketing.events.dto.EventMessage;
import com.kostyantynverchenko.ticketing.events.dto.EventPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class EventPublisher {

    @Value("${kafka.events.topic:events.events}")
    private String eventsTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public EventPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishEvent(EventPayload eventPayload, String eventType) {
        try {
            EventMessage eventMessage = new EventMessage(
                    UUID.randomUUID(),
                    eventType,
                    "Event",
                    eventPayload.getId(),
                    objectMapper.writeValueAsString(eventPayload)
            );

            String json = objectMapper.writeValueAsString(eventMessage);
            kafkaTemplate.send(eventsTopic, eventPayload.getId().toString(), json).get();
        } catch (Exception e) {
            log.error("Error publishing event for event {}: {}", eventPayload.getId(), e.getMessage());
        }
    }
}
