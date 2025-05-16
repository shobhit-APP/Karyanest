package com.backend.karyanestApplication.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BotMessageRequest {
    private Long conversationId; // The conversation where the bot is responding
    private String botMessage;   // Bot-generated message content
}
