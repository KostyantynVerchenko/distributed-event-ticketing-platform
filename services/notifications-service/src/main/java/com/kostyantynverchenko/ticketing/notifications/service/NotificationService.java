package com.kostyantynverchenko.ticketing.notifications.service;

import com.kostyantynverchenko.ticketing.notifications.dto.CreateNotificationRequest;
import com.kostyantynverchenko.ticketing.notifications.entity.Notification;
import com.kostyantynverchenko.ticketing.notifications.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> getAllNotifications() {
        log.info("Find all notifications");

        return notificationRepository.findAll();
    }

    public List<Notification> getNotificationsByUserId(UUID userId) {
        log.info("Find all notifications by user id {}", userId);

        return notificationRepository.findByUserId(userId);
    }

    public Notification getNotificationById(UUID id) {
        log.info("Find notification by id {}", id);

        return notificationRepository.findById(id).orElseThrow(() -> new RuntimeException("Notification with id " + id + " not found"));
    }

    public Notification create(CreateNotificationRequest request) {
        log.info("Create notification {}; from Kafka event", request);

        Notification notification = new Notification();

        notification.setOrderId(request.getOrderId());
        notification.setUserId(request.getUserId());
        notification.setType(request.getType());
        notification.setMessage(request.getMessage());
        notification.setStatus(request.getNotificationStatus());

        return notificationRepository.save(notification);
    }
}
