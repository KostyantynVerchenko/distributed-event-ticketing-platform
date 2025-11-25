package com.kostyantynverchenko.ticketing.orders.service;

import com.kostyantynverchenko.ticketing.orders.entity.TicketReservation;
import com.kostyantynverchenko.ticketing.orders.repository.TicketReservationRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class TicketReservationService {

    private static final int MAX_RETRIES = 3;

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

        try {
            log.info("Creating Ticket Reservation for eventId: {}", eventId);
            return ticketReservationRepository.save(ticketReservation);
        } catch (DataIntegrityViolationException e) {
            log.warn("Concurrent creation of Ticket Reservation for eventId: {}", eventId);
            return getTicketReservationByEventUuid(eventId);
        }
    }

    @Transactional
    public TicketReservation addReservedTicketsByEvent(UUID eventId, Integer quantity) {
        int attempts = 0;

        while (true) {
            attempts++;
            try {
                TicketReservation ticketReservation = getTicketReservationByEventUuid(eventId);

                if (ticketReservation.getReservedTickets() + ticketReservation.getSoldTickets() + quantity > ticketReservation.getTotalTickets()) {
                    throw new RuntimeException("Not enough tickets left");
                }

                ticketReservation.setReservedTickets(ticketReservation.getReservedTickets() + quantity);

                log.info("Adding Reserved Tickets for eventId: {}", eventId);

                return ticketReservationRepository.save(ticketReservation);
            } catch (OptimisticLockException | ObjectOptimisticLockingFailureException e) {
                log.warn("Optimistic locking conflict on addReservedTicketsByEvent for eventId: {} (attempt {}/{})", eventId, attempts, MAX_RETRIES);
                if (attempts >= MAX_RETRIES) {
                    log.error("Max retries reached on addReservedTicketsByEvent for eventId: {}",  eventId);
                    throw new OptimisticLockingFailureException("Optimistic locking conflict");
                }
            }
        }
    }

    @Transactional
    public TicketReservation removeReservedTicketsByEvent(UUID eventId, Integer quantity) {
        TicketReservation ticketReservation = getTicketReservationByEventUuid(eventId);

        if (ticketReservation.getReservedTickets() - quantity < 0) {
            throw  new RuntimeException("Cannot remove reserved tickets below 0");
        } else {
            ticketReservation.setReservedTickets(ticketReservation.getReservedTickets() - quantity);
        }

        log.info("Removing Reserved Tickets for eventId: {}", eventId);

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

        log.info("Updating Sold Tickets for eventId: {}", eventId);

        return ticketReservationRepository.save(ticketReservation);
    }
}
