package com.backend.karyanestApplication.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class MessageRequest {
    private Long conversationId;       // Conversation ID
    private String message;// Message content
    private ZonedDateTime timestamp;// Optional timestamp to fetch messages after

    // No need to manually define getters/setters due to Lombok
}
