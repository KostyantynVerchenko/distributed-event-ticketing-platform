package com.kostyantynverchenko.ticketing.orders.exception;

import java.util.UUID;

public class EventNotAvailableException extends RuntimeException {

    private UUID eventId;

    public EventNotAvailableException(UUID eventId) {
        super("Event with id " + eventId + " is not available");
        this.eventId = eventId;
    }

    public UUID getEventId() {
        return eventId;
    }
}
