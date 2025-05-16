package com.example.notification.Services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FCMService {
    public void sendNotification(String token, String title, String message) {
        if (token == null || token.trim().isEmpty()) {
            log.error("FCM token is null or empty");
            return;
        }
        log.info("FCM notification token for the sending: {}", token);
        try {
            Message fcmMessage = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(message)
                            .build())
                    .setToken(token)
                    .build();
            String response = FirebaseMessaging.getInstance().send(fcmMessage);
            log.info("FCM notification sent: {} ", response);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send FCM notification: {}, Token: {}", e.getMessage(), token);
        } catch (Exception e) {
            log.error("Unexpected error sending FCM notification: {}", e.getMessage());
        }
    }
}
