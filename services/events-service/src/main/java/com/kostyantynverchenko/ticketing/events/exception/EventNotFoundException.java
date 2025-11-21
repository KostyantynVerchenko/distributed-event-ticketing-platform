package com.kostyantynverchenko.ticketing.events.exception;

import java.util.UUID;

public class EventNotFoundException extends RuntimeException {

    private final UUID eventId;

    public EventNotFoundException(UUID eventId) {
        super("Event with id " + eventId + " not found");
        this.eventId = eventId;
    }

    public UUID getEventId() {
        return eventId;
    }
}
