package com.example.notification.Services;

import com.example.notification.DTO.NotificationRequestDTO;
import com.example.notification.DTO.NotificationResponse;
import com.example.notification.Model.Notification;
import com.example.notification.Repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationsService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private KafkaTemplate<String, Notification> kafkaTemplate;

    private static final String TOPIC = "notifications";

    @Transactional
    public NotificationResponse createNotification(NotificationRequestDTO request) {

        Notification notification = new Notification();
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setUser(request.getUserId());
        notification.setNotificationType(request.getNotificationType());
        notification.setGlobal(request.isGlobal());

        Notification savedNotification = notificationRepository.save(notification);
//        kafkaTemplate.send(TOPIC, savedNotification.getId().toString(), savedNotification);
        try {
            kafkaTemplate.send(TOPIC, savedNotification.getId().toString(), savedNotification);
            log.info("Sent notification to Kafka topic '{}': {}", TOPIC, savedNotification);
        } catch (Exception e) {
            log.error("Failed to send notification to Kafka: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send notification to Kafka", e);
        }
        return new NotificationResponse(savedNotification);
    }
}
