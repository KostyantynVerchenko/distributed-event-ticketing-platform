package com.kostyantynverchenko.ticketing.notifications.controller;

import com.kostyantynverchenko.ticketing.notifications.dto.NotificationResponse;
import com.kostyantynverchenko.ticketing.notifications.entity.Notification;
import com.kostyantynverchenko.ticketing.notifications.service.JwtService;
import com.kostyantynverchenko.ticketing.notifications.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtService jwtService;

    public NotificationController(NotificationService notificationService, JwtService jwtService) {
        this.notificationService = notificationService;
        this.jwtService = jwtService;
    }

    @GetMapping("/notifications")
    public ResponseEntity<?> getNotifications(@RequestHeader("Authorization") String header) {
        log.info("Request to get all notifications");

        String token = header.substring(7);
        UUID userId = jwtService.extractUserId(token);

        List<Notification> notificationsList = notificationService.getNotificationsByUserId(userId);
        List<NotificationResponse> notifications = notificationsList.stream().map(NotificationResponse::new).toList();

        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/notifications/{id}")
    public ResponseEntity<?> getNotificationById(@RequestHeader("Authorization") String header, @PathVariable UUID id) {
        log.info("Request to get notification by id {}", id);

        String token = header.substring(7);
        UUID userIdFromRequest = jwtService.extractUserId(token);

        Notification notification = notificationService.getNotificationById(id);

        if (!userIdFromRequest.equals(notification.getUserId())) {
            throw new RuntimeException("You have no such notification");
        } else {
            NotificationResponse notificationResponse = new NotificationResponse(notification);
            return ResponseEntity.ok(notificationResponse);
        }
    }
}
