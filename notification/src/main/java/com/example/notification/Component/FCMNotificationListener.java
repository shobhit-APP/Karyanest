package com.example.notification.Component;

import com.example.notification.Model.FCMToken;
import com.example.notification.Model.Notification;
import com.example.notification.Repository.FCMTokenRepository;
import com.example.notification.Services.FCMService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class FCMNotificationListener {

    @Autowired
    private FCMService fcmService;

    @Autowired
    private FCMTokenRepository fcmTokenRepository;

    @KafkaListener(topics = "notifications", groupId = "notification-group")
    public void handleNotification(Notification notification) {
       try {
           log.info("Received notification: {}", notification);
           if (notification == null) {
               log.error("Received null notification");
               return;
           }
           if (notification.getStatus() != Notification.NotificationStatus.ACTIVE) {
               log.info("Skipping inactive notification: {}", notification.getId());
               return;
           }
           if (notification.isGlobal()) {
               List<FCMToken> tokens = fcmTokenRepository.findAll();
               for (FCMToken fcmToken : tokens) {
                   fcmService.sendNotification(fcmToken.getToken(), notification.getTitle(), notification.getMessage());
               }
           } else {
               Long userId = notification.getUser();
               if (userId == null) {
                   log.error("Notification user is null for notification ID: {}", notification.getId());
                   return;
               }
               List<FCMToken> tokens = fcmTokenRepository.findByUser(userId); // Assuming findByUserId takes Long
               if (tokens.isEmpty()) {
                   log.warn("No FCM tokens found for user ID: {}", userId);
                   return;
               }
               for (FCMToken fcmToken : tokens) {
                   fcmService.sendNotification(fcmToken.getToken(), notification.getTitle(), notification.getMessage());
               }
           }
       }catch (Exception e) {
           log.error("Failed to process notification ID: {} - Error: {}", notification != null ? notification.getId() : "unknown", e.getMessage(), e);
           throw new RuntimeException("Failed to process notification", e);
       }
    }
}
