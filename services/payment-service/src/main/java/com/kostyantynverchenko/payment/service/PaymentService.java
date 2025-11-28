package com.kostyantynverchenko.payment.service;

import com.kostyantynverchenko.payment.dto.CreatePaymentRequest;
import com.kostyantynverchenko.payment.entity.Payment;
import com.kostyantynverchenko.payment.entity.PaymentStatus;
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

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> getAllPayments() {
        log.info("Request to get all payments");
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(UUID paymentId) {
        log.info("Request to get payment by id {}", paymentId);
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found!"));
    }

    public List<Payment> getPaymentByOrderId(UUID orderId) {
        log.info("Request to get payment by order id {}", orderId);
        return paymentRepository.findByOrderId(orderId);
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

        return paymentRepository.save(payment);
    }
}
