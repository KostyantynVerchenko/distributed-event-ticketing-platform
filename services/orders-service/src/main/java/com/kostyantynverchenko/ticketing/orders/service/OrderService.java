package com.kostyantynverchenko.ticketing.orders.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kostyantynverchenko.ticketing.orders.client.events.EventResponse;
import com.kostyantynverchenko.ticketing.orders.client.events.EventStatus;
import com.kostyantynverchenko.ticketing.orders.client.events.EventsServiceClient;
import com.kostyantynverchenko.ticketing.orders.dto.CreateOrderRequest;
import com.kostyantynverchenko.ticketing.orders.dto.EventPayload;
import com.kostyantynverchenko.ticketing.orders.entity.*;
import com.kostyantynverchenko.ticketing.orders.exception.*;
import com.kostyantynverchenko.ticketing.orders.repository.OrderRepository;
import com.kostyantynverchenko.ticketing.orders.repository.OutboxEventRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final EventsServiceClient eventsServiceClient;
    private final TicketReservationService ticketReservationService;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepository,
                        EventsServiceClient eventsServiceClient,
                        TicketReservationService ticketReservationService,
                        OutboxEventRepository outboxEventRepository,
                        ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.eventsServiceClient = eventsServiceClient;
        this.ticketReservationService = ticketReservationService;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    private void publishOrderEvent(String eventType, Order order) {
        try {
            EventPayload payload = new EventPayload(order.getId(), order.getUserId(), order.getOrderStatus().name(),order.getTotalAmount());

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

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    @Transactional
    public Order createOrder(CreateOrderRequest dto) {
        Order order = new Order();
        OrderItem orderItem = new OrderItem();
        EventResponse eventResponse = eventsServiceClient.findById(dto.getEventId());

        if (eventResponse.getStatus() != EventStatus.AVAILABLE) {
            throw new EventNotAvailableException(eventResponse.getId());
        }

        if (!ticketReservationService.reservationExistsForEvent(eventResponse.getId())) {
            ticketReservationService.createTicketReservationByEvent(eventResponse.getId(), eventResponse.getTicketsAvailable());
        }

        ticketReservationService.addReservedTicketsByEvent(eventResponse.getId(), dto.getQuantity());
        orderItem.setQuantity(dto.getQuantity());

        orderItem.setEventId(dto.getEventId());
        orderItem.setUnitPrice(eventResponse.getPrice());
        orderItem.setEventTitleSnapshot(eventResponse.getTitle());
        orderItem.setEventDateSnapshot(eventResponse.getDate());
        orderItem.setTicketPriceSnapshot(eventResponse.getPrice());
        orderItem.setStatus(OrderItemStatus.RESERVED);

        order.addOrderItem(orderItem);
        order.setUserId(UUID.randomUUID());
        order.setOrderStatus(OrderStatus.CREATED);

        BigDecimal totAm = BigDecimal.valueOf(orderItem.getQuantity()).multiply(eventResponse.getPrice());
        order.setTotalAmount(totAm);

        order.setReservedUntil(LocalDateTime.now().plusMinutes(15));

        orderRepository.save(order);

        publishOrderEvent("ORDER_CREATED", order);

        return order;
    }

    @Transactional
    public void cancelPayment(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        OrderStatus orderStatus = order.getOrderStatus();

        if (order.getOrderStatus().equals(OrderStatus.CREATED) || order.getOrderStatus().equals(OrderStatus.PENDING_PAYMENT)) {
            for (OrderItem orderItem : order.getOrderItems()) {
                ticketReservationService.removeReservedTicketsByEvent(orderItem.getEventId(), orderItem.getQuantity());
                orderItem.setStatus(OrderItemStatus.RELEASED);
            }
            order.setOrderStatus(OrderStatus.CANCELLED);

            publishOrderEvent("ORDER_CANCELLED", order);
        }
        else {
            throw new InvalidOrderStateException(orderId, order.getOrderStatus());
        }
        log.info("Order {} has been cancelled", orderId);
    }


    @Transactional
    public void startPayment(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getReservedUntil() != null && order.getReservedUntil().isBefore(LocalDateTime.now())) {
            throw new OrderExpiredException(orderId);
        }

        if (order.getOrderStatus().equals(OrderStatus.CREATED)) {
            order.setOrderStatus(OrderStatus.PENDING_PAYMENT);
        }
        else {
            throw new InvalidOrderStateException(orderId, order.getOrderStatus());
        }

        log.info("Order {} has been moved to PENDING_PAYMENT", orderId);
    }

    @Transactional
    public void finishPayment(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getOrderStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new InvalidOrderStateException(orderId, order.getOrderStatus());
        }

        if (order.getReservedUntil() != null && order.getReservedUntil().isBefore(LocalDateTime.now())) {
            for (OrderItem orderItem : order.getOrderItems()) {
                ticketReservationService.removeReservedTicketsByEvent(orderItem.getEventId(), orderItem.getQuantity());
                orderItem.setStatus(OrderItemStatus.RELEASED);
            }
            order.setOrderStatus(OrderStatus.EXPIRED);
            publishOrderEvent("ORDER_EXPIRED", order);
            return;
        }

        Random random = new  Random();
        int tempImitationOfPayment = random.nextInt(2) + 1; // super basic payment service emulation

        if (order.getOrderStatus().equals(OrderStatus.PENDING_PAYMENT) && tempImitationOfPayment == 1) {
            for (OrderItem orderItem : order.getOrderItems()) {
                ticketReservationService.updateSoldTicketsByEvent(orderItem.getEventId(), orderItem.getQuantity());
                orderItem.setStatus(OrderItemStatus.CONFIRMED);
            }
            order.setOrderStatus(OrderStatus.PAID);
            publishOrderEvent("ORDER_PAID", order);
        }
        else if (order.getOrderStatus().equals(OrderStatus.PENDING_PAYMENT) && tempImitationOfPayment == 2) {
            for (OrderItem orderItem : order.getOrderItems()) {
                ticketReservationService.removeReservedTicketsByEvent(orderItem.getEventId(), orderItem.getQuantity());
                orderItem.setStatus(OrderItemStatus.RELEASED);
            }
            order.setOrderStatus(OrderStatus.FAILED);
            publishOrderEvent("ORDER_FAILED", order);
        }
        else {
            throw new InvalidOrderStateException(orderId, order.getOrderStatus());
        }
    }
}
