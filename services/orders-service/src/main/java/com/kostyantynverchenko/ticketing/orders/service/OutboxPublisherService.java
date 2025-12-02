package com.kostyantynverchenko.ticketing.orders.service;

import com.kostyantynverchenko.ticketing.orders.dto.OrderEventMessage;
import com.kostyantynverchenko.ticketing.orders.entity.OutboxEvent;
import com.kostyantynverchenko.ticketing.orders.entity.OutboxEventStatus;
import com.kostyantynverchenko.ticketing.orders.repository.OutboxEventRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@Slf4j
@Service
public class OutboxPublisherService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.orders.topic:orders.events}")
    private String ordersTopic;

    public OutboxPublisherService(OutboxEventRepository outboxEventRepository, ObjectMapper objectMapper, KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelayString = "5000")
    @Transactional
    public void eventPublisher() {
        List<OutboxEvent> events = outboxEventRepository.findByStatus(OutboxEventStatus.NEW);

        if (events.isEmpty()) {
            return;
        }

        log.info("Found {} orders to publish",  events.size());

        for (OutboxEvent event : events) {
            try {

                OrderEventMessage orderEventMessage = new OrderEventMessage(event.getId(), event.getEventType(), event.getAggregateType(), event.getAggregateId(), event.getPayload());
                String json = objectMapper.writeValueAsString(orderEventMessage);

                kafkaTemplate.send(ordersTopic, event.getAggregateId().toString(), json).get();

                log.info("Publishing event to Kafka: id={}, type={}, aggregateType={}, aggregateId={}, payload={}", event.getId(), event.getEventType(), event.getAggregateType(), event.getAggregateId(), event.getPayload());
                event.setStatus(OutboxEventStatus.SENT);
            } catch (Exception ex) {
                log.error("Error publishing event {}: {}", event.getId(), ex.getMessage(), ex);
                event.setStatus(OutboxEventStatus.FAILED);
            }
        }
    }
}
