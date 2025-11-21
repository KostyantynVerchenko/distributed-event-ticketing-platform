package com.kostyantynverchenko.ticketing.events.service;

import com.kostyantynverchenko.ticketing.events.dto.CreateEventRequestDto;
import com.kostyantynverchenko.ticketing.events.dto.EventResponseDto;
import com.kostyantynverchenko.ticketing.events.dto.PagedResponse;
import com.kostyantynverchenko.ticketing.events.entity.Event;
import com.kostyantynverchenko.ticketing.events.entity.EventStatus;
import com.kostyantynverchenko.ticketing.events.exception.EventNotFoundException;
import com.kostyantynverchenko.ticketing.events.repository.EventRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
/*
    public List<Event> getAllEvents() {
        log.debug("Get all events");
        return eventRepository.findAllByEventStatusNot(EventStatus.DELETED);
    }
 */

    public PagedResponse<EventResponseDto> getAllEvents(int page, int size) {
        log.debug("Get all events with page {} and size {}", page, size);

        Sort sort = Sort.by(Sort.Direction.ASC, "date");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Event> events = eventRepository.findAllByEventStatusNot(EventStatus.DELETED, pageable);

        List<EventResponseDto> content = events.getContent().stream().map(EventResponseDto::new).toList();

        return new PagedResponse<>(content, events.getNumber(), events.getSize(), events.getTotalElements(), events.getTotalPages(), events.isLast());
    };

    public Event getEventById(Long id) {
        log.debug("Get event by id {}", id);
        return eventRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Event not found with id {}", id);
                    return new EventNotFoundException(id);
                });
    }

    @Transactional
    public Event createEvent(CreateEventRequestDto createEventRequestDto) {
        log.debug("Create event: title = {}, date = {}", createEventRequestDto.getTitle(), createEventRequestDto.getDate());

        Event event = new Event();

        event.setTitle(createEventRequestDto.getTitle());
        event.setDate(createEventRequestDto.getDate());
        event.setPrice(createEventRequestDto.getPrice());
        event.setTicketsAvailable(createEventRequestDto.getTicketsAvailable());
        event.setEventStatus(createEventRequestDto.getStatus());

        eventRepository.save(event);

        return event;
    }

    @Transactional
    public Event updateEvent(Long id, CreateEventRequestDto createEventRequestDto) {
        log.debug("Update event: id = {}", id);

        Event event = getEventById(id);

        event.setTitle(createEventRequestDto.getTitle());
        event.setDate(createEventRequestDto.getDate());
        event.setPrice(createEventRequestDto.getPrice());
        event.setTicketsAvailable(createEventRequestDto.getTicketsAvailable());
        event.setEventStatus(createEventRequestDto.getStatus());

        eventRepository.save(event);

        return event;
    }

    @Transactional
    public void deleteEvent(Long id) {
        log.debug("Delete event: id = {}", id);
        Event event = getEventById(id);

        event.setEventStatus(EventStatus.DELETED);

        eventRepository.save(event);
    }
}
