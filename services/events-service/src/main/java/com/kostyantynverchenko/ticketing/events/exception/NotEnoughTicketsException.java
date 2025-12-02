package com.kostyantynverchenko.ticketing.events.exception;

import java.util.UUID;

public class NotEnoughTicketsException extends RuntimeException {
    public NotEnoughTicketsException(UUID eventId) {
        super("Not enough tickets available for event: " + eventId);
    }
}
