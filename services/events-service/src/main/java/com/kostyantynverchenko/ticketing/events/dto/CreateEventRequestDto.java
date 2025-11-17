package com.kostyantynverchenko.ticketing.events.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateEventRequestDto {
    private String title;
    private LocalDate date;
    private BigDecimal price;
    private int ticketsAvailable;

    public CreateEventRequestDto() {}

    public CreateEventRequestDto(String title, LocalDate date, BigDecimal price, int ticketsAvailable) {
        this.title = title;
        this.date = date;
        this.price = price;
        this.ticketsAvailable = ticketsAvailable;
    }
}
