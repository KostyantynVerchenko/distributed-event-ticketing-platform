package com.kostyantynverchenko.ticketing.events.exception;

import java.time.Instant;

public class ErrorResponse {

    private String code;
    private String message;
    private Instant timestamp;

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = Instant.now();
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
