package com.kostyantynverchenko.payment.controller;

import com.kostyantynverchenko.payment.dto.CreatePaymentRequest;
import com.kostyantynverchenko.payment.entity.Payment;
import com.kostyantynverchenko.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/payments/{id}")
    public Payment getPayment(@PathVariable UUID id) {
        return paymentService.getPaymentById(id);
    }

    @GetMapping("/payments/by-order/{orderId}")
    public List<Payment> getPaymentsByOrderId(@PathVariable UUID orderId) {
        return paymentService.getPaymentByOrderId(orderId);
    }

    @PostMapping("/payments")
    public Payment createPayment(@RequestBody @Valid CreatePaymentRequest request) {
        return paymentService.createAndProcessPayment(request);
    }
}
