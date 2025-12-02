package com.kostyantynverchenko.ticketing.orders.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kostyantynverchenko.ticketing.orders.dto.EventPayload;
import com.kostyantynverchenko.ticketing.orders.entity.*;
import com.kostyantynverchenko.ticketing.orders.exception.OrderNotFoundException;
import com.kostyantynverchenko.ticketing.orders.exception.OutboxSerializationException;
import com.kostyantynverchenko.ticketing.orders.repository.OrderRepository;
import com.kostyantynverchenko.ticketing.orders.repository.OutboxEventRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ReservationExpirationService {

    private final OrderRepository orderRepository;
    private final TicketReservationService ticketReservationService;
    private final ObjectMapper objectMapper;
    private final OutboxEventRepository outboxEventRepository;

    public ReservationExpirationService(OrderRepository orderRepository,
                                        TicketReservationService ticketReservationService,
                                        ObjectMapper objectMapper,
                                        OutboxEventRepository outboxEventRepository) {
        this.orderRepository = orderRepository;
        this.ticketReservationService = ticketReservationService;
        this.objectMapper = objectMapper;
        this.outboxEventRepository = outboxEventRepository;
    }

    private void publishOrderEvent(String eventType, Order order) {
        try {
            EventPayload payload = new EventPayload(order.getId(), order.getUserId(), order.getOrderStatus().name(),order.getTotalAmount(), order.getOrderCurrency().name());

            String payloadJson = objectMapper.writeValueAsString(payload);

            OutboxEvent outboxEvent = new OutboxEvent();
            outboxEvent.setAggregateType("Order");
            outboxEvent.setAggregateId(order.getId());
            outboxEvent.setEventType(eventType);
            outboxEvent.setPayload(payloadJson);
            outboxEvent.setStatus(OutboxEventStatus.NEW);

            outboxEventRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new OutboxSerializationException(eventType, order.getId(), e);
        }
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
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!order.getOrderStatus().equals(OrderStatus.PENDING_PAYMENT) && !order.getOrderStatus().equals(OrderStatus.CREATED)) {
            return;
        }

        if (order.getReservedUntil() == null || order.getReservedUntil().isAfter(LocalDateTime.now())) {
            return;
        }

        for (OrderItem orderItem : order.getOrderItems()) {
            ticketReservationService.removeReservedTicketsByEvent(orderItem.getEventId(), orderItem.getQuantity());
            orderItem.setStatus(OrderItemStatus.RELEASED);
        }

        order.setOrderStatus(OrderStatus.EXPIRED);

        publishOrderEvent("ORDER_EXPIRED", order);

        log.info("Order {} has been expired", orderId);
    }
}
