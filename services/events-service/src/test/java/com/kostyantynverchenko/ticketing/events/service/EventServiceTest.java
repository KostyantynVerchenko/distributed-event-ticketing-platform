package com.kostyantynverchenko.ticketing.events.service;

import com.kostyantynverchenko.ticketing.events.dto.CreateEventRequestDto;
import com.kostyantynverchenko.ticketing.events.entity.Event;
import com.kostyantynverchenko.ticketing.events.entity.EventStatus;
import com.kostyantynverchenko.ticketing.events.exception.EventNotFoundException;
import com.kostyantynverchenko.ticketing.events.repository.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private EventService eventService;

    @Test
    @DisplayName("Create event test")
    void createEvent() {
        String title = "Test title";
        LocalDate date = LocalDate.now();
        BigDecimal price = new BigDecimal("100");
        int ticketsAvailable = 100;
        EventStatus eventStatus = EventStatus.AVAILABLE;

        CreateEventRequestDto dto = new CreateEventRequestDto(title, date, price, ticketsAvailable, eventStatus);

        eventService.createEvent(dto);

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(eventCaptor.capture());

        Event event = eventCaptor.getValue();

        assertEquals(title, event.getTitle());
        assertEquals(date, event.getDate());
    }

    @Test
    @DisplayName("Get event by invalid id")
    void getEventById() {
        UUID id = UUID.randomUUID();
        when(eventRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventService.getEventById(id));
    }
}