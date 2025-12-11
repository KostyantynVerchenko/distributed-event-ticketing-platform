package com.kostyantynverchenko.ticketing.events.service;

import com.kostyantynverchenko.ticketing.events.dto.CreateEventRequestDto;
import com.kostyantynverchenko.ticketing.events.dto.EventPayload;
import com.kostyantynverchenko.ticketing.events.dto.EventResponseDto;
import com.kostyantynverchenko.ticketing.events.dto.PagedResponse;
import com.kostyantynverchenko.ticketing.events.entity.Event;
import com.kostyantynverchenko.ticketing.events.entity.EventStatus;
import com.kostyantynverchenko.ticketing.events.exception.EventNotFoundException;
import com.kostyantynverchenko.ticketing.events.exception.NotEnoughTicketsException;
import com.kostyantynverchenko.ticketing.events.repository.EventRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class EventService {
    private final EventRepository eventRepository;
    private final EventPublisher eventPublisher;

    public EventService(EventRepository eventRepository, EventPublisher eventPublisher) {
        this.eventRepository = eventRepository;
        this.eventPublisher = eventPublisher;
    }
/*
    public List<Event> getAllEvents() {
        log.debug("Get all events");
        return eventRepository.findAllByEventStatusNot(EventStatus.DELETED);
    }
 */

    @Cacheable(value = "eventsPage",key = "#page + '-' + #size")
    public PagedResponse<EventResponseDto> getAllEvents(int page, int size) {
        log.debug("Get all events with page {} and size {}", page, size);

        Sort sort = Sort.by(Sort.Direction.ASC, "date");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Event> events = eventRepository.findAllByEventStatusNot(EventStatus.DELETED, pageable);

        List<EventResponseDto> content = events.getContent().stream().map(EventResponseDto::new).toList();

        return new PagedResponse<>(content, events.getNumber(), events.getSize(), events.getTotalElements(), events.getTotalPages(), events.isLast());
    }

    @Cacheable(value = "eventById", key = "#id")
    public Event getEventById(UUID id) {
        log.debug("Get event by id {}", id);
        return eventRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Event not found with id {}", id);
                    return new EventNotFoundException(id);
                });
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "eventsPage", allEntries = true),
            @CacheEvict(value = "eventById", key = "#result.id", condition = "#result != null")
    })
    public Event createEvent(CreateEventRequestDto createEventRequestDto) {
        log.debug("Create event: title = {}, date = {}", createEventRequestDto.getTitle(), createEventRequestDto.getDate());

        Event event = new Event();

        event.setTitle(createEventRequestDto.getTitle());
        event.setDate(createEventRequestDto.getDate());
        event.setPrice(createEventRequestDto.getPrice());
        event.setTicketsAvailable(createEventRequestDto.getTicketsAvailable());
        event.setEventStatus(createEventRequestDto.getStatus());

        eventRepository.save(event);

        publishEvent(event);

        return event;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "eventsPage", allEntries = true),
            @CacheEvict(value = "eventById", key = "#id")
    })
    public Event updateEvent(UUID id, CreateEventRequestDto createEventRequestDto) {
        log.debug("Update event: id = {}", id);

        Event event = getEventById(id);

        event.setTitle(createEventRequestDto.getTitle());
        event.setDate(createEventRequestDto.getDate());
        event.setPrice(createEventRequestDto.getPrice());
        event.setTicketsAvailable(createEventRequestDto.getTicketsAvailable());
        event.setEventStatus(createEventRequestDto.getStatus());

        eventRepository.save(event);

        publishEvent(event);

        return event;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "eventsPage", allEntries = true),
            @CacheEvict(value = "eventById", key = "#id")
    })
    public void deleteEvent(UUID id) {
        log.debug("Delete event: id = {}", id);
        Event event = getEventById(id);

        event.setEventStatus(EventStatus.DELETED);

        eventRepository.save(event);

        publishEvent(event);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "eventsPage", allEntries = true),
            @CacheEvict(value = "eventById", key = "#id")
    })
    public Event reduceAvailableTickets(UUID id, int quantity) {
        log.debug("Reduce tickets for event: id = {}, quantity = {}", id, quantity);

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Event event = getEventById(id);

        int updatedTickets = event.getTicketsAvailable() - quantity;
        if (updatedTickets < 0) {
            throw new NotEnoughTicketsException(id);
        }

        event.setTicketsAvailable(updatedTickets);

        return eventRepository.save(event);
    }

    public void publishEvent(Event event) {
        EventPayload eventPayload = new EventPayload();

        eventPayload.setId(event.getId());
        eventPayload.setTitle(event.getTitle());
        eventPayload.setDate(event.getDate());
        eventPayload.setPrice(event.getPrice());
        eventPayload.setStatus(event.getEventStatus());

        String eventType;

        switch (event.getEventStatus()) {
            case CANCELLED -> eventType = "EVENT_CANCELLED";
            case AVAILABLE -> eventType = "EVENT_AVAILABLE";
            case EXPIRED -> eventType = "EVENT_EXPIRED";
            case DELETED -> eventType = "EVENT_DELETED";
            default -> {
                return;
            }
        }

        eventPublisher.publishEvent(eventPayload, eventType);
    }
}
