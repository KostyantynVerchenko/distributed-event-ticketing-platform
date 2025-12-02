package com.kostyantynverchenko.ticketing.events.dto;

import jakarta.validation.constraints.Min;

public class UpdateTicketsRequestDto {

    @Min(1)
    private int quantity;

    public UpdateTicketsRequestDto() {

    }

    public UpdateTicketsRequestDto(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
