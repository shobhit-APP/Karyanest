package com.example.notification.DTO;

import com.example.notification.Model.Notification;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;
import java.time.ZonedDateTime;

@Data
public class NotificationResponse {
    private Long id;
    private String title;
    private String message;
    private Long userId;
    private String notificationType;
    private boolean isGlobal;
    private String status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kolkata")
    private
    ZonedDateTime createdAt;

    public NotificationResponse(Notification notification) {
        this.id = notification.getId();
        this.title = notification.getTitle();
        this.message = notification.getMessage();
        this.userId = notification.getUser() != null ? notification.getUser() : null;
        this.notificationType = notification.getNotificationType().toString();
        this.isGlobal = notification.isGlobal();
        this.status = notification.getStatus().toString();
        this.createdAt = notification.getCreatedAt();
    }
}
