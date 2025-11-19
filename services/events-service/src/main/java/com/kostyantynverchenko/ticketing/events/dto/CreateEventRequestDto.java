package com.kostyantynverchenko.ticketing.events.dto;

import com.kostyantynverchenko.ticketing.events.entity.EventStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateEventRequestDto {
    @NotNull
    @NotBlank
    private String title;

    @Future
    @NotNull
    private LocalDate date;

    @PositiveOrZero
    @NotNull
    private BigDecimal price;

    @PositiveOrZero
    private int ticketsAvailable;

    @NotNull
    private EventStatus status;

    public CreateEventRequestDto() {}

    public CreateEventRequestDto(String title, LocalDate date, BigDecimal price, int ticketsAvailable, EventStatus status) {
        this.title = title;
        this.date = date;
        this.price = price;
        this.ticketsAvailable = ticketsAvailable;
        this.status = status;
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

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }
}
