package com.kostyantynverchenko.ticketing.events.dto;

import com.kostyantynverchenko.ticketing.events.entity.Event;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EventResponseDto {
    private Long id;
    private String title;
    private LocalDate date;
    private BigDecimal price;
    private int ticketsAvailable;

    public EventResponseDto() {}

    public EventResponseDto(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.date = event.getDate();
        this.price = event.getPrice();
        this.ticketsAvailable = event.getTicketsAvailable();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public int getTicketsAvailable() {
        return ticketsAvailable;
    }

    public void setTicketsAvailable(int ticketsAvailable) {
        this.ticketsAvailable = ticketsAvailable;
    }
}
