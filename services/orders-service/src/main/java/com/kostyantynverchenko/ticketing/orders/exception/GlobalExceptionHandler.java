package com.kostyantynverchenko.ticketing.orders.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFoundException(OrderNotFoundException ex) {
        log.error("Error: {}", ex.getMessage());

        ErrorResponse body = new ErrorResponse("ORDER_NOT_FOUND", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(OutboxSerializationException.class)
    public ResponseEntity<ErrorResponse> handleOutboxSerializationException(OutboxSerializationException ex) {
        log.error("Error: {}", ex.getMessage());

        ErrorResponse body = new ErrorResponse("OUT_BOX_SERIALIZATION_ERROR", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(EventNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleEventNotAvailableException(EventNotAvailableException ex) {
        log.error("Error: {}", ex.getMessage());

        ErrorResponse body = new ErrorResponse("EVENT_NOT_AVAILABLE", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(NotEnoughTicketsException.class)
    public ResponseEntity<ErrorResponse> handleNotEnoughTicketsException(NotEnoughTicketsException ex) {
        log.error("Error: {}", ex.getMessage());

        ErrorResponse body = new ErrorResponse("NOT_ENOUGH_TICKETS", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(InvalidOrderStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrderStateException(InvalidOrderStateException ex) {
        log.error("Error: {}", ex.getMessage());

        ErrorResponse body = new ErrorResponse("INVALID_ORDER_STATE", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(OrderExpiredException.class)
    public ResponseEntity<ErrorResponse> handleOrderExpiredException(OrderExpiredException ex) {
        log.error("Error: {}", ex.getMessage());

        ErrorResponse body = new ErrorResponse("ORDER_EXPIRED", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
}
