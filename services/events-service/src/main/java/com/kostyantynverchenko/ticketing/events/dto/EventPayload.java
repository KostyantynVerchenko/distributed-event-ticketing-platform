package com.kostyantynverchenko.ticketing.events.dto;

import com.kostyantynverchenko.ticketing.events.entity.EventStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class EventPayload {
    private UUID id;
    private String title;
    private LocalDate date;
    private BigDecimal price;
    private EventStatus status;

    public EventPayload() {

    }

    public EventPayload(UUID id, String title, LocalDate date, BigDecimal price, EventStatus status) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.price = price;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }
}
