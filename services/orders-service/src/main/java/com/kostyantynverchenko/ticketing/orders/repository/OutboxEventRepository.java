package com.kostyantynverchenko.ticketing.orders.repository;

import com.kostyantynverchenko.ticketing.orders.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
}
