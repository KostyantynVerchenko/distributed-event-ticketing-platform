package com.kostyantynverchenko.ticketing.orders.exception;

import java.util.UUID;

public class OutboxSerializationException extends RuntimeException {

    private final UUID aggregateId;
    private final String eventType;

    public OutboxSerializationException(String eventType, UUID aggregateId, Throwable cause) {
        super("Failed to serialize outbox event with type: " + eventType + "; aggregate id: " + aggregateId, cause);
        this.aggregateId = aggregateId;
        this.eventType = eventType;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public String getEventType() {
        return eventType;
    }
}
