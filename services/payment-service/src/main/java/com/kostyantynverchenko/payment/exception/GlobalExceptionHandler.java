package com.kostyantynverchenko.payment.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePaymentNotFoundException(PaymentNotFoundException e) {
        log.error("Error: {}", e.getMessage());

        ErrorResponse body = new ErrorResponse("PAYMENT_NOT_FOUND", e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}
