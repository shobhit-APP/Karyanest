package com.example.notification.DTO;

import lombok.Data;

@Data
public class UpdateTokenRequestDTO {
    private Long userId;
    private String fcmToken;
    private String deviceId;
}
