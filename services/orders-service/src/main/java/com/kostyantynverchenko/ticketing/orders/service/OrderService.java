package com.kostyantynverchenko.ticketing.orders.service;

import com.kostyantynverchenko.ticketing.orders.client.events.EventResponse;
import com.kostyantynverchenko.ticketing.orders.client.events.EventStatus;
import com.kostyantynverchenko.ticketing.orders.client.events.EventsServiceClient;
import com.kostyantynverchenko.ticketing.orders.dto.CreateOrderRequest;
import com.kostyantynverchenko.ticketing.orders.entity.Order;
import com.kostyantynverchenko.ticketing.orders.entity.OrderItem;
import com.kostyantynverchenko.ticketing.orders.entity.OrderStatus;
import com.kostyantynverchenko.ticketing.orders.entity.TicketReservation;
import com.kostyantynverchenko.ticketing.orders.exception.OrderNotFoundException;
import com.kostyantynverchenko.ticketing.orders.repository.OrderRepository;
import com.kostyantynverchenko.ticketing.orders.repository.TicketReservationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final EventsServiceClient eventsServiceClient;
    private final TicketReservationService ticketReservationService;

    public OrderService(OrderRepository orderRepository, EventsServiceClient eventsServiceClient,  TicketReservationService ticketReservationService) {
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

        order.addOrderItem(orderItem);
        order.setUserId(UUID.randomUUID());

        BigDecimal totAm = BigDecimal.valueOf(orderItem.getQuantity()).multiply(eventResponse.getPrice());
        order.setTotalAmount(totAm);

        order.setOrderStatus(OrderStatus.PENDING_PAYMENT);

        order.setReservedUntil(LocalDateTime.now().plusMinutes(15));

        orderRepository.save(order);

        return order;
    }
}
