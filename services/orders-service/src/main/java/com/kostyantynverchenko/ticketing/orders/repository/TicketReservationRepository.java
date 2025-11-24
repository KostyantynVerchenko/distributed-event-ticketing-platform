package com.kostyantynverchenko.ticketing.orders.repository;

import com.kostyantynverchenko.ticketing.orders.entity.TicketReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketReservationRepository extends JpaRepository<TicketReservation, UUID> {
    boolean existsByEventId(UUID eventId);

    Optional<TicketReservation> findByEventId(UUID eventId);
}
