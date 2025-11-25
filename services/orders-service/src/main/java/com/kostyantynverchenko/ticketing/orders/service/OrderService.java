package com.kostyantynverchenko.ticketing.orders.service;

import com.kostyantynverchenko.ticketing.orders.client.events.EventResponse;
import com.kostyantynverchenko.ticketing.orders.client.events.EventStatus;
import com.kostyantynverchenko.ticketing.orders.client.events.EventsServiceClient;
import com.kostyantynverchenko.ticketing.orders.dto.CreateOrderRequest;
import com.kostyantynverchenko.ticketing.orders.entity.Order;
import com.kostyantynverchenko.ticketing.orders.entity.OrderItem;
import com.kostyantynverchenko.ticketing.orders.entity.OrderItemStatus;
import com.kostyantynverchenko.ticketing.orders.entity.OrderStatus;
import com.kostyantynverchenko.ticketing.orders.exception.OrderNotFoundException;
import com.kostyantynverchenko.ticketing.orders.repository.OrderItemRepository;
import com.kostyantynverchenko.ticketing.orders.repository.OrderRepository;
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

    public OrderService(OrderRepository orderRepository,
                        EventsServiceClient eventsServiceClient,
                        TicketReservationService ticketReservationService) {
        this.orderRepository = orderRepository;
        this.eventsServiceClient = eventsServiceClient;
        this.ticketReservationService = ticketReservationService;
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
            throw new RuntimeException("Event is not available");
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
        }
        else {
            throw new RuntimeException("Cannot cancel order in status: " + orderStatus); // temporary, will add custom exception in future
        }
        log.info("Order {} has been cancelled", orderId);
    }


    @Transactional
    public void startPayment(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getReservedUntil() != null && order.getReservedUntil().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Order is expired"); // temp exception
        }

        if (order.getOrderStatus().equals(OrderStatus.CREATED)) {
            order.setOrderStatus(OrderStatus.PENDING_PAYMENT);
        }
        else {
            throw new RuntimeException("Order cannot be started");
        }

        log.info("Order {} has been moved to PENDING_PAYMENT", orderId);
    }

    @Transactional
    public void finishPayment(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getOrderStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new RuntimeException("Order is expired");
        }

        if (order.getReservedUntil() != null && order.getReservedUntil().isBefore(LocalDateTime.now())) {
            for (OrderItem orderItem : order.getOrderItems()) {
                ticketReservationService.removeReservedTicketsByEvent(orderItem.getEventId(), orderItem.getQuantity());
                orderItem.setStatus(OrderItemStatus.RELEASED);
            }
            order.setOrderStatus(OrderStatus.EXPIRED);
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
        }
        else if (order.getOrderStatus().equals(OrderStatus.PENDING_PAYMENT) && tempImitationOfPayment == 2) {
            for (OrderItem orderItem : order.getOrderItems()) {
                ticketReservationService.removeReservedTicketsByEvent(orderItem.getEventId(), orderItem.getQuantity());
                orderItem.setStatus(OrderItemStatus.RELEASED);
            }
            order.setOrderStatus(OrderStatus.FAILED);
        }
        else {
            throw new RuntimeException("Order cannot be finished");
        }
    }
}
