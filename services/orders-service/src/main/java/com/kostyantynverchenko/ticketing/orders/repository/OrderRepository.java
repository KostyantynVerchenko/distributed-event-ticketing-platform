package com.kostyantynverchenko.ticketing.orders.repository;

import com.kostyantynverchenko.ticketing.orders.entity.Order;
import com.kostyantynverchenko.ticketing.orders.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByOrderStatusInAndReservedUntilBefore(List<OrderStatus> orderStatuses, LocalDateTime reservedUntil);
}