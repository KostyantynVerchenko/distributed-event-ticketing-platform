package com.kostyantynverchenko.ticketing.events.exception;

public class EventNotFoundException extends RuntimeException {

    private final Long eventId;

    public EventNotFoundException(Long eventId) {
        super("Event with id " + eventId + " not found");
        this.eventId = eventId;
    }

    public Long getEventId() {
        return eventId;
    }
}
