package com.kostyantynverchenko.ticketing.orders.client.events;

public class UpdateTicketsRequest {

    private int quantity;

    public UpdateTicketsRequest() {

    }

    public UpdateTicketsRequest(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
