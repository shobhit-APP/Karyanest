package com.example.notification.DTO;

import com.example.notification.Model.Notification;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NotificationRequestDTO {
    private String title;
    private String message;
    private Long userId;
    private Notification.NotificationType notificationType;
//    private String notificationType;
    @JsonProperty("isGlobal")
    private boolean isGlobal;
}
