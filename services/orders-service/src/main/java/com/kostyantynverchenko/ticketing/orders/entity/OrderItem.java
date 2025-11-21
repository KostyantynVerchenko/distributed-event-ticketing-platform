package com.kostyantynverchenko.ticketing.orders.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderItemStatus status =  OrderItemStatus.RESERVED;

    @Column(name = "event_title_snapshot")
    private String eventTitleSnapshot;

    @Column(name = "event_date_snapshot")
    private LocalDateTime eventDateSnapshot;

    @Column(name = "ticket_price_snapshot")
    private BigDecimal ticketPriceSnapshot;

    public OrderItem() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public OrderItemStatus getStatus() {
        return status;
    }

    public void setStatus(OrderItemStatus status) {
        this.status = status;
    }

    public String getEventTitleSnapshot() {
        return eventTitleSnapshot;
    }

    public void setEventTitleSnapshot(String eventTitleSnapshot) {
        this.eventTitleSnapshot = eventTitleSnapshot;
    }

    public LocalDateTime getEventDateSnapshot() {
        return eventDateSnapshot;
    }

    public void setEventDateSnapshot(LocalDateTime eventDateSnapshot) {
        this.eventDateSnapshot = eventDateSnapshot;
    }

    public BigDecimal getTicketPriceSnapshot() {
        return ticketPriceSnapshot;
    }

    public void setTicketPriceSnapshot(BigDecimal ticketPriceSnapshot) {
        this.ticketPriceSnapshot = ticketPriceSnapshot;
    }
}
