package com.kostyantynverchenko.ticketing.orders.service;

import com.kostyantynverchenko.ticketing.orders.entity.OutboxEvent;
import com.kostyantynverchenko.ticketing.orders.entity.OutboxEventStatus;
import com.kostyantynverchenko.ticketing.orders.repository.OutboxEventRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OutboxPublisherService {

    private final OutboxEventRepository outboxEventRepository;

    public OutboxPublisherService(OutboxEventRepository outboxEventRepository) {
        this.outboxEventRepository = outboxEventRepository;
    }

    @Scheduled(fixedDelayString = "5000")
    @Transactional
    public void eventPublisher() {
        int attempts = 0;

        List<OutboxEvent> events = outboxEventRepository.findByStatus(OutboxEventStatus.NEW);

        if (events.isEmpty()) {
            return;
        }

        log.info("Found {} orders to publish",  events.size());

        for (OutboxEvent event : events) {
            try {
                log.info("Publishing event: id={}, type={}, aggregateType={}, aggregateId={}, payload={}", event.getId(), event.getEventType(), event.getAggregateType(), event.getAggregateId(), event.getPayload());
                event.setStatus(OutboxEventStatus.SENT);
            } catch (Exception ex) {
                log.error("Error publishing event {}: {}", event.getId(), ex.getMessage(), ex);
                event.setStatus(OutboxEventStatus.FAILED);
            }
        }
    }
}
