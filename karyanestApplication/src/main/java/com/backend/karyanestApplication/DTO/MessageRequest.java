package com.backend.karyanestApplication.DTO;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
public class MessageRequest {
    private Long conversationId;       // Conversation ID
    private String message;// Message content
    private LocalDateTime timestamp;  // Optional timestamp to fetch messages after

    // No need to manually define getters/setters due to Lombok
}
