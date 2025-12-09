package com.kostyantynverchenko.ticketing.notifications.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kostyantynverchenko.ticketing.notifications.entity.Notification;
import com.kostyantynverchenko.ticketing.notifications.entity.NotificationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationResponse {
    private UUID userId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID orderId;
    private String type;
    private String message;
    private NotificationStatus notificationStatus;
    private LocalDateTime timestamp;

    public NotificationResponse() {
    }

    public NotificationResponse(Notification notification) {
        this.userId = notification.getUserId();
        this.orderId = notification.getOrderId();
        this.type = notification.getType();
        this.message = notification.getMessage();
        this.notificationStatus = notification.getStatus();
        this.timestamp = notification.getCreatedAt();
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationStatus getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(NotificationStatus notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
