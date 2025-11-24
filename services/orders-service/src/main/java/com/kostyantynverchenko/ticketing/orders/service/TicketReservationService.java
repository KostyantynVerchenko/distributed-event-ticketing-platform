package com.kostyantynverchenko.ticketing.orders.service;

import com.kostyantynverchenko.ticketing.orders.entity.TicketReservation;
import com.kostyantynverchenko.ticketing.orders.repository.TicketReservationRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class TicketReservationService {

    private final TicketReservationRepository ticketReservationRepository;

    public TicketReservationService(TicketReservationRepository ticketReservationRepository) {
        this.ticketReservationRepository = ticketReservationRepository;
    }

    public boolean reservationExistsForEvent(UUID eventId) {
        return ticketReservationRepository.existsByEventId(eventId);
    }

    public TicketReservation getTicketReservationByEventUuid(UUID eventId) {
        return ticketReservationRepository.findByEventId(eventId).orElseThrow(() -> new RuntimeException("Ticket Reservation not found"));
    }

    @Transactional
    public TicketReservation createTicketReservationByEvent(UUID eventId, Integer eventTotalTickets) {
        TicketReservation ticketReservation = new TicketReservation();

        ticketReservation.setEventId(eventId);
        ticketReservation.setTotalTickets(eventTotalTickets);
        ticketReservation.setReservedTickets(0);
        ticketReservation.setSoldTickets(0);

        return ticketReservationRepository.save(ticketReservation);
    }

    @Transactional
    public TicketReservation addReservedTicketsByEvent(UUID eventId, Integer quantity) {
        TicketReservation ticketReservation = getTicketReservationByEventUuid(eventId);

        if (ticketReservation.getReservedTickets() + ticketReservation.getSoldTickets() + quantity > ticketReservation.getTotalTickets()) {
            throw new RuntimeException("Not enough tickets left");
        }

        ticketReservation.setReservedTickets(ticketReservation.getReservedTickets() + quantity);

        return ticketReservationRepository.save(ticketReservation);
    }

    @Transactional
    public TicketReservation removeReservedTicketsByEvent(UUID eventId, Integer quantity) {
        TicketReservation ticketReservation = getTicketReservationByEventUuid(eventId);

        if (ticketReservation.getReservedTickets() - quantity < 0) {
            throw  new RuntimeException("Cannot remove reserved tickets below 0");
        } else {
            ticketReservation.setReservedTickets(ticketReservation.getReservedTickets() - quantity);
        }

        return ticketReservationRepository.save(ticketReservation);
    }

    @Transactional
    public TicketReservation updateSoldTicketsByEvent(UUID eventId, Integer quantity) {
        TicketReservation ticketReservation = getTicketReservationByEventUuid(eventId);

        if (ticketReservation.getReservedTickets() < quantity) {
            throw  new RuntimeException("Not enough tickets in reservation");
        }

        ticketReservation.setReservedTickets(ticketReservation.getReservedTickets() - quantity);
        ticketReservation.setSoldTickets(ticketReservation.getSoldTickets() + quantity);

        return ticketReservationRepository.save(ticketReservation);
    }
}
