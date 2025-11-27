package com.kostyantynverchenko.ticketing.orders.service;

import com.kostyantynverchenko.ticketing.orders.entity.TicketReservation;
import com.kostyantynverchenko.ticketing.orders.exception.NotEnoughTicketsException;
import com.kostyantynverchenko.ticketing.orders.repository.TicketReservationRepository;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketReservationServiceTest {

    private static final UUID CORRECT_ID = UUID.randomUUID();
    private static final UUID INCORRECT_ID = UUID.randomUUID();

    @Mock
    private TicketReservationRepository ticketReservationRepository;

    @InjectMocks
    private TicketReservationService ticketReservationService;

    @Test
    @DisplayName("Check if reservation exists")
    void reservationExistsForEvent() {
        when(ticketReservationRepository.existsByEventId(CORRECT_ID)).thenReturn(true);
        when(ticketReservationRepository.existsByEventId(INCORRECT_ID)).thenReturn(false);

        assertTrue(ticketReservationService.reservationExistsForEvent(CORRECT_ID));
        assertFalse(ticketReservationService.reservationExistsForEvent(INCORRECT_ID));

        verify(ticketReservationRepository).existsByEventId(CORRECT_ID);
        verify(ticketReservationRepository).existsByEventId(INCORRECT_ID);
    }

    @Test
    @DisplayName("getTicketReservationByEventUuid must return entity if exists and throw exception if not")
    void getTicketReservationByEventUuid() {
        TicketReservation ticketReservation = new TicketReservation(CORRECT_ID, 100, 10, 5);

        when(ticketReservationRepository.findByEventId(CORRECT_ID)).thenReturn(Optional.of(ticketReservation));
        when(ticketReservationRepository.findByEventId(INCORRECT_ID)).thenReturn(Optional.empty());

        Optional<TicketReservation> actual = Optional.ofNullable(ticketReservationService.getTicketReservationByEventUuid(CORRECT_ID));

        assertNotNull(actual);
        assertEquals(ticketReservation, actual.get());
        assertThrows(RuntimeException.class, () -> ticketReservationService.getTicketReservationByEventUuid(INCORRECT_ID));

        verify(ticketReservationRepository).findByEventId(CORRECT_ID);
        verify(ticketReservationRepository).findByEventId(INCORRECT_ID);
    }

    @Test
    @DisplayName("Create new TicketReservation if none exists")
    void createTicketReservationByEvent() {
        when(ticketReservationRepository.save(any(TicketReservation.class))).thenAnswer(i -> i.getArgument(0));

        int totalTickets = 100;

        TicketReservation result = ticketReservationService.createTicketReservationByEvent(CORRECT_ID, totalTickets);

        assertNotNull(result);
        assertEquals(CORRECT_ID, result.getEventId());
        assertEquals(totalTickets, result.getTotalTickets());
        assertEquals(0, result.getReservedTickets());
        assertEquals(0, result.getSoldTickets());

        verify(ticketReservationRepository).save(any(TicketReservation.class));
    }

    @Test
    @DisplayName("Create new TicketReservation if one already exists, must return existing one")
    void createTicketReservationByExistingEvent() {
        TicketReservation existingReservation = new TicketReservation(CORRECT_ID, 100, 0, 0);

        when(ticketReservationRepository.save(any(TicketReservation.class))).thenThrow(new DataIntegrityViolationException("Ticket Reservation already exists"));
        when(ticketReservationRepository.findByEventId(CORRECT_ID)).thenReturn(Optional.of(existingReservation));

        TicketReservation result = ticketReservationService.createTicketReservationByEvent(CORRECT_ID, 100);

        assertSame(existingReservation, result);

        verify(ticketReservationRepository).save(any(TicketReservation.class));
        verify(ticketReservationRepository).findByEventId(CORRECT_ID);
    }

    @Test
    void addReservedTicketsByEventSuccess() {
        TicketReservation ticketReservation = new TicketReservation(CORRECT_ID, 100, 75, 20);

        when(ticketReservationRepository.save(any(TicketReservation.class))).thenAnswer(i -> i.getArgument(0));
        when(ticketReservationRepository.findByEventId(CORRECT_ID)).thenReturn(Optional.of(ticketReservation));

        int quantity = 5;

        ticketReservationService.addReservedTicketsByEvent(CORRECT_ID, quantity);

        assertEquals(80, ticketReservation.getReservedTickets());
        assertEquals(20, ticketReservation.getSoldTickets());
        assertEquals(100, ticketReservation.getTotalTickets());

        verify(ticketReservationRepository).save(any(TicketReservation.class));
        verify(ticketReservationRepository).findByEventId(CORRECT_ID);
    }

    @Test
    void addReservedTicketsByEventNotEnoughTickets() {
        TicketReservation ticketReservation = new TicketReservation(CORRECT_ID, 100, 80, 20);

        when(ticketReservationRepository.findByEventId(CORRECT_ID)).thenReturn(Optional.of(ticketReservation));

        int quantity = 5;

        assertThrows(NotEnoughTicketsException.class, () -> ticketReservationService.addReservedTicketsByEvent(CORRECT_ID, quantity));

        verify(ticketReservationRepository).findByEventId(CORRECT_ID);
    }

    @Test
    void addReservedTicketsByEventOptimisticLockRetry() {
        TicketReservation first = new TicketReservation(CORRECT_ID, 100, 10, 0);
        TicketReservation second = new TicketReservation(CORRECT_ID, 100, 10, 0);

        when(ticketReservationRepository.findByEventId(CORRECT_ID)).thenReturn(Optional.of(first)).thenReturn(Optional.of(second));
        when(ticketReservationRepository.save(any(TicketReservation.class))).thenThrow(new OptimisticLockException("conflict")).thenAnswer(i -> i.getArgument(0));

        int quantity = 5;

        TicketReservation result = ticketReservationService.addReservedTicketsByEvent(CORRECT_ID, quantity);

        assertNotNull(result);
        assertEquals(15, result.getReservedTickets());
        assertEquals(0, result.getSoldTickets());
        assertEquals(100, result.getTotalTickets());

        verify(ticketReservationRepository, times(2)).findByEventId(CORRECT_ID);
        verify(ticketReservationRepository, times(2)).save(any(TicketReservation.class));
    }

    @Test
    void removeReservedTicketsByEventSuccess() {
        TicketReservation ticketReservation = new TicketReservation(CORRECT_ID, 100, 10, 5);

        when(ticketReservationRepository.save(any(TicketReservation.class))).thenAnswer(i -> i.getArgument(0));
        when(ticketReservationRepository.findByEventId(CORRECT_ID)).thenReturn(Optional.of(ticketReservation));

        int quantity = 10;

        ticketReservationService.removeReservedTicketsByEvent(CORRECT_ID, quantity);

        assertEquals(0, ticketReservation.getReservedTickets());
        assertEquals(5, ticketReservation.getSoldTickets());
        assertEquals(100, ticketReservation.getTotalTickets());

        verify(ticketReservationRepository).save(any(TicketReservation.class));
        verify(ticketReservationRepository).findByEventId(CORRECT_ID);
    }

    @Test
    void removeReservedTicketsByEventFailure() {
        TicketReservation ticketReservation = new TicketReservation(CORRECT_ID, 100, 10, 20);

        when(ticketReservationRepository.findByEventId(CORRECT_ID)).thenReturn(Optional.of(ticketReservation));

        int quantity = 25;

        assertThrows(NotEnoughTicketsException.class, () -> ticketReservationService.removeReservedTicketsByEvent(CORRECT_ID, quantity));

        verify(ticketReservationRepository).findByEventId(CORRECT_ID);
    }

    @Test
    void updateSoldTicketsByEventSuccess() {
        TicketReservation ticketReservation = new TicketReservation(CORRECT_ID, 100, 80, 20);

        when(ticketReservationRepository.save(any(TicketReservation.class))).thenAnswer(i -> i.getArgument(0));
        when(ticketReservationRepository.findByEventId(CORRECT_ID)).thenReturn(Optional.of(ticketReservation));

        int quantity = 80;

        ticketReservationService.updateSoldTicketsByEvent(CORRECT_ID, quantity);

        assertEquals(0, ticketReservation.getReservedTickets());
        assertEquals(100, ticketReservation.getSoldTickets());
        assertEquals(100, ticketReservation.getTotalTickets());

        verify(ticketReservationRepository).save(any(TicketReservation.class));
        verify(ticketReservationRepository).findByEventId(CORRECT_ID);
    }

    @Test
    void updateSoldTicketsByEventFailure() {
        TicketReservation ticketReservation = new TicketReservation(CORRECT_ID, 100, 80, 20);

        when(ticketReservationRepository.findByEventId(CORRECT_ID)).thenReturn(Optional.of(ticketReservation));

        int quantity = 85;

        assertThrows(NotEnoughTicketsException.class, () -> ticketReservationService.updateSoldTicketsByEvent(CORRECT_ID, quantity));

        verify(ticketReservationRepository).findByEventId(CORRECT_ID);
    }
}