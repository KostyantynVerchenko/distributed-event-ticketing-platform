package com.kostyantynverchenko.ticketing.events.controller;

import com.kostyantynverchenko.ticketing.events.dto.CreateEventRequestDto;
import com.kostyantynverchenko.ticketing.events.dto.EventResponseDto;
import com.kostyantynverchenko.ticketing.events.entity.Event;
import com.kostyantynverchenko.ticketing.events.service.EventService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping("/api")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public ResponseEntity<?> findAll() {
        log.info("Find all events request");
        List<Event> events = eventService.getAllEvents();
        List<EventResponseDto> eventResponseDtos = events.stream().map(EventResponseDto::new).toList();

        return ok(eventResponseDtos);
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        log.info("Find event by id = {} request", id);
        EventResponseDto eventResponseDto = new EventResponseDto(eventService.getEventById(id));

        return ok(eventResponseDto);
    }

    @PostMapping("/events")
    public ResponseEntity<?> createEvent(@Valid @RequestBody CreateEventRequestDto createEventRequestDto) {
        log.info("Create event request: title = {}, date = {}", createEventRequestDto.getTitle(), createEventRequestDto.getDate());
        EventResponseDto eventResponseDto = new EventResponseDto(eventService.createEvent(createEventRequestDto));

        return ok(eventResponseDto);
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @Valid @RequestBody CreateEventRequestDto createEventRequestDto) {
        log.info("Update event request: id = {}, title = {}", id, createEventRequestDto.getTitle());

        EventResponseDto eventResponseDto = new EventResponseDto(eventService.updateEvent(id, createEventRequestDto));

        return ok(eventResponseDto);
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        log.info("Delete event by id = {} request", id);

        eventService.deleteEvent(id);

        return ResponseEntity.ok().build();
    }
}
