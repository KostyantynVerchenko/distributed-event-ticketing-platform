package com.kostyantynverchenko.ticketing.events.controller;

import com.kostyantynverchenko.ticketing.events.dto.EventResponseDto;
import com.kostyantynverchenko.ticketing.events.entity.Event;
import com.kostyantynverchenko.ticketing.events.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public ResponseEntity<?> findAll() {
        List<Event> events = eventService.getAllEvents();
        List<EventResponseDto> eventResponseDtos = events.stream().map(EventResponseDto::new).toList();

        return ok(eventResponseDtos);
    }
}
