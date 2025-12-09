package com.kostyantynverchenko.ticketing.notifications.dto;

import com.kostyantynverchenko.ticketing.notifications.entity.NotificationStatus;

import java.util.UUID;

public class CreateNotificationRequest {
    private UUID orderId;
    private UUID userId;
    private String type;
    private String message;
    private NotificationStatus notificationStatus;

    public CreateNotificationRequest() {
    }

    public CreateNotificationRequest(UUID orderId, UUID userId, String type, String message, NotificationStatus notificationStatus) {
        this.orderId = orderId;
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.notificationStatus = notificationStatus;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
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
}
