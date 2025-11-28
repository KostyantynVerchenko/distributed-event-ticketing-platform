package com.kostyantynverchenko.ticketing.orders.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kostyantynverchenko.ticketing.orders.client.events.EventResponse;
import com.kostyantynverchenko.ticketing.orders.client.events.EventStatus;
import com.kostyantynverchenko.ticketing.orders.client.events.EventsServiceClient;
import com.kostyantynverchenko.ticketing.orders.client.payment.PaymentServiceClient;
import com.kostyantynverchenko.ticketing.orders.dto.CreateOrderRequest;
import com.kostyantynverchenko.ticketing.orders.entity.*;
import com.kostyantynverchenko.ticketing.orders.exception.EventNotAvailableException;
import com.kostyantynverchenko.ticketing.orders.exception.OrderExpiredException;
import com.kostyantynverchenko.ticketing.orders.repository.OrderRepository;
import com.kostyantynverchenko.ticketing.orders.repository.OutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private static final UUID ORDER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private EventsServiceClient eventsServiceClient;

    @Mock
    private TicketReservationService ticketReservationService;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, eventsServiceClient, ticketReservationService, outboxEventRepository, objectMapper, paymentServiceClient);
    }

    @Test
    void createOrderSuccess() {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setEventId(EVENT_ID);
        createOrderRequest.setQuantity(2);

        EventResponse eventResponse = new EventResponse();
        eventResponse.setId(EVENT_ID);
        eventResponse.setTitle("Test event");
        eventResponse.setDate(LocalDate.now().plusDays(5));
        eventResponse.setPrice(BigDecimal.TEN);
        eventResponse.setTicketsAvailable(100);
        eventResponse.setStatus(EventStatus.AVAILABLE);

        when(eventsServiceClient.findById(EVENT_ID)).thenReturn(eventResponse);
        when(ticketReservationService.reservationExistsForEvent(EVENT_ID)).thenReturn(false);
        when(ticketReservationService.createTicketReservationByEvent(EVENT_ID, 100)).thenReturn(null);
        when(ticketReservationService.addReservedTicketsByEvent(EVENT_ID, 2)).thenReturn(null);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.createOrder(createOrderRequest);

        assertNotNull(result);
        assertEquals(OrderStatus.CREATED, result.getOrderStatus());
        assertNotNull(result.getUserId());
        assertNotNull(result.getReservedUntil());
        assertEquals(1, result.getOrderItems().size());

        OrderItem item = result.getOrderItems().get(0);
        assertEquals(EVENT_ID, item.getEventId());
        assertEquals(2, item.getQuantity());
        assertEquals(BigDecimal.TEN, item.getUnitPrice());
        assertEquals(OrderItemStatus.RESERVED, item.getStatus());

        assertEquals(new BigDecimal("20"), result.getTotalAmount());

        verify(eventsServiceClient).findById(EVENT_ID);
        verify(ticketReservationService).reservationExistsForEvent(EVENT_ID);
        verify(ticketReservationService).createTicketReservationByEvent(EVENT_ID, 100);
        verify(ticketReservationService).addReservedTicketsByEvent(EVENT_ID, 2);
        verify(orderRepository).save(any(Order.class));
        verify(outboxEventRepository).save(any(OutboxEvent.class));
    }

    @Test
    void createOrderEventNotAvailable() {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setEventId(EVENT_ID);
        createOrderRequest.setQuantity(1);

        EventResponse eventResponse = new EventResponse();
        eventResponse.setId(EVENT_ID);
        eventResponse.setStatus(EventStatus.CANCELLED);

        when(eventsServiceClient.findById(EVENT_ID)).thenReturn(eventResponse);

        assertThrows(EventNotAvailableException.class, () -> orderService.createOrder(createOrderRequest));

        verify(eventsServiceClient).findById(EVENT_ID);
        verify(ticketReservationService, never()).addReservedTicketsByEvent(any(), any());
        verify(orderRepository, never()).save(any());
        verify(outboxEventRepository, never()).save(any());
    }

    @Test
    void cancelPaymentFromCreated() {
        Order order = new Order();
        order.setId(ORDER_ID);
        order.setOrderStatus(OrderStatus.CREATED);

        OrderItem orderItem = new OrderItem();
        orderItem.setEventId(EVENT_ID);
        orderItem.setQuantity(3);
        orderItem.setStatus(OrderItemStatus.RESERVED);

        order.addOrderItem(orderItem);

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        orderService.cancelPayment(ORDER_ID);

        assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
        assertEquals(OrderItemStatus.RELEASED, order.getOrderItems().get(0).getStatus());

        verify(ticketReservationService).removeReservedTicketsByEvent(EVENT_ID, 3);
        verify(outboxEventRepository).save(any(OutboxEvent.class));
    }

    @Test
    void startPaymentExpired() {
        Order order = new Order();

        order.setId(ORDER_ID);
        order.setOrderStatus(OrderStatus.CREATED);
        order.setReservedUntil(LocalDateTime.now().minusMinutes(1));

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        assertThrows(OrderExpiredException.class,
                () -> orderService.startPayment(ORDER_ID));
    }
}