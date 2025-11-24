package com.kostyantynverchenko.ticketing.orders.dto;

import com.kostyantynverchenko.ticketing.orders.entity.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class OrderItemResponse {
    private UUID id;
    private UUID eventId;
    private String eventTitleSnapshot;
    private LocalDate eventDateSnapshot;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal ticketPriceSnapshot;

    public OrderItemResponse(OrderItem orderItem) {
        this.id = orderItem.getId();
        this.eventId = orderItem.getEventId();
        this.eventTitleSnapshot = orderItem.getEventTitleSnapshot();
        this.eventDateSnapshot = orderItem.getEventDateSnapshot();
        this.quantity = orderItem.getQuantity();
        this.unitPrice = orderItem.getUnitPrice();
        this.ticketPriceSnapshot = orderItem.getTicketPriceSnapshot();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getEventTitleSnapshot() {
        return eventTitleSnapshot;
    }

    public void setEventTitleSnapshot(String eventTitleSnapshot) {
        this.eventTitleSnapshot = eventTitleSnapshot;
    }

    public LocalDate getEventDateSnapshot() {
        return eventDateSnapshot;
    }

    public void setEventDateSnapshot(LocalDate eventDateSnapshot) {
        this.eventDateSnapshot = eventDateSnapshot;
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

    public BigDecimal getTicketPriceSnapshot() {
        return ticketPriceSnapshot;
    }

    public void setTicketPriceSnapshot(BigDecimal ticketPriceSnapshot) {
        this.ticketPriceSnapshot = ticketPriceSnapshot;
    }
}
