package com.kostyantynverchenko.ticketing.orders.repository;

import com.kostyantynverchenko.ticketing.orders.entity.OutboxEvent;
import com.kostyantynverchenko.ticketing.orders.entity.OutboxEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findByStatus(OutboxEventStatus status);
}
