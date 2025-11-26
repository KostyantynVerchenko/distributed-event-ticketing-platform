package com.kostyantynverchenko.ticketing.orders.exception;

import java.util.UUID;

public class NotEnoughTicketsException extends RuntimeException {

    private UUID eventId;

    public NotEnoughTicketsException(UUID eventId) {
        super("Not enough tickets for event: " + eventId);
        this.eventId = eventId;
    }

    public UUID getEventId() {
        return eventId;
    }
}
