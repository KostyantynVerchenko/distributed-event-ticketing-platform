package com.kostyantynverchenko.payment.service;

import com.kostyantynverchenko.payment.dto.CreatePaymentRequest;
import com.kostyantynverchenko.payment.dto.event.PaymentEventPayload;
import com.kostyantynverchenko.payment.entity.Payment;
import com.kostyantynverchenko.payment.entity.PaymentStatus;
import com.kostyantynverchenko.payment.exception.PaymentNotFoundException;
import com.kostyantynverchenko.payment.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher paymentEventPublisher;

    public PaymentService(PaymentRepository paymentRepository, PaymentEventPublisher paymentEventPublisher) {
        this.paymentRepository = paymentRepository;
        this.paymentEventPublisher = paymentEventPublisher;
    }

    public List<Payment> getAllPayments() {
        log.info("Request to get all payments");
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(UUID paymentId) {
        log.info("Request to get payment by id {}", paymentId);
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));
    }

    public List<Payment> getPaymentByOrderId(UUID orderId) {
        log.info("Request to get payment by order id {}", orderId);
        return paymentRepository.findByOrderId(orderId);
    }

    public List<Payment> getAllPayments(UUID orderId) {
        log.info("Request to get all payments");
        return paymentRepository.findAll();
    }

    @Transactional
    public Payment createAndProcessPayment(CreatePaymentRequest request) {
        log.info("Request to create payment {}", request);

        Payment payment = new Payment();

        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setStatus(PaymentStatus.PENDING);

        payment = paymentRepository.save(payment);

        boolean success = ThreadLocalRandom.current().nextInt(100) < 80;

        if (success) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setFailureReason(null);
            log.info("Payment = {} for order = {} succeeded", payment.getId(), request.getOrderId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Payment was declined by the bank");
            log.info("Payment = {} for order = {} failed - {}", payment.getId(), request.getOrderId(),  payment.getFailureReason());
        }

        payment = paymentRepository.save(payment);

        publishPaymentEvent(payment);
        return payment;
    }

    public void publishPaymentEvent(Payment payment) {
        PaymentEventPayload paymentEventPayload = new PaymentEventPayload();

        paymentEventPayload.setPaymentId(payment.getId());
        paymentEventPayload.setOrderId(payment.getOrderId());
        paymentEventPayload.setAmount(payment.getAmount());
        paymentEventPayload.setPaymentStatus(payment.getStatus());
        paymentEventPayload.setFailureReason(payment.getFailureReason());

        String eventType = payment.getStatus() == PaymentStatus.SUCCESS ? "PAYMENT_SUCCESS" : "PAYMENT_FAILED";
        paymentEventPublisher.publishPaymentEvent(paymentEventPayload, eventType);
    }
}
