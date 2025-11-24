package com.kostyantynverchenko.ticketing.orders.service;

import com.kostyantynverchenko.ticketing.orders.entity.Order;
import com.kostyantynverchenko.ticketing.orders.entity.OrderItem;
import com.kostyantynverchenko.ticketing.orders.entity.OrderItemStatus;
import com.kostyantynverchenko.ticketing.orders.entity.OrderStatus;
import com.kostyantynverchenko.ticketing.orders.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ReservationExpirationService {

    private final OrderRepository orderRepository;
    private final TicketReservationService ticketReservationService;

    public ReservationExpirationService(OrderRepository orderRepository, TicketReservationService ticketReservationService) {
        this.orderRepository = orderRepository;
        this.ticketReservationService = ticketReservationService;
    }

    @Scheduled(fixedDelayString = "60000")
    public void checkOrderExpiration() {
        List<Order> orders = orderRepository.findByOrderStatusInAndReservedUntilBefore(
                List.of(OrderStatus.CREATED, OrderStatus.PENDING_PAYMENT), LocalDateTime.now()
        );

        if (orders.isEmpty()) {
            return;
        }

        log.info("Found {} orders to expire",  orders.size());

        for (Order order : orders) {
            try {
                expireSingleOrder(order.getId());
            } catch (Exception e) {
                log.error("Failed to expire single order {}",  e.getMessage());
            }
        }
    }

    @Transactional
    public void expireSingleOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getOrderStatus().equals(OrderStatus.PENDING_PAYMENT) || !order.getOrderStatus().equals(OrderStatus.CREATED)) {
            return;
        }

        if (order.getReservedUntil().isAfter(LocalDateTime.now())) {
            return;
        }

        for (OrderItem orderItem : order.getOrderItems()) {
            ticketReservationService.removeReservedTicketsByEvent(orderItem.getEventId(), orderItem.getQuantity());
            orderItem.setStatus(OrderItemStatus.RELEASED);
        }

        order.setOrderStatus(OrderStatus.EXPIRED);

        log.info("Order {} has been expired", orderId);
    }
}
