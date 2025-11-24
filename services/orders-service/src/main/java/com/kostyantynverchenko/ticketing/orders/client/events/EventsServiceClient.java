package com.kostyantynverchenko.ticketing.orders.client.events;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.UUID;

@Service
public class EventsServiceClient {
    private final RestClient restClient;

    public EventsServiceClient(RestClient restClient) {
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
}
