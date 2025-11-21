package com.kostyantynverchenko.ticketing.events.repository;

import com.kostyantynverchenko.ticketing.events.entity.Event;
import com.kostyantynverchenko.ticketing.events.entity.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    Page<Event> findAllByEventStatusNot(EventStatus status, Pageable pageable);
}
