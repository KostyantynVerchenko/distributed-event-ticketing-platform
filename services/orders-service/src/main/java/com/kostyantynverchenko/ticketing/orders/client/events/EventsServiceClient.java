package com.kostyantynverchenko.ticketing.orders.client.events;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.UUID;

@Service
public class EventsServiceClient {
    private final RestClient restClient;

    public EventsServiceClient(@Qualifier("eventsRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public EventResponse findById(UUID id) {
        try {
            return restClient.get()
                    .uri("/api/events/{id}", id)
                    .retrieve()
                    .body(EventResponse.class);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("Event not found: " + id);
            }
            throw new RuntimeException("Failed to call events service: " + e.getMessage(), e);
        }
    }

    public void reduceAvailableTickets(UUID id, int quantity) {
        try {
            restClient.post()
                    .uri("/api/events/{id}/tickets/sell", id)
                    .body(new UpdateTicketsRequest(quantity))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            throw new RuntimeException("Failed to update tickets for event %s: %s".formatted(id, e.getMessage()), e);
        }
    }
}
