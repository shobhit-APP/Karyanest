package com.backend.karyanestApplication.Service;

import com.example.Authentication.DTO.JWTUserDTO;
import com.backend.karyanestApplication.Model.User;
import com.backend.karyanestApplication.Model.UserNotification;
import com.backend.karyanestApplication.Repositry.UserNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private UserNotificationRepository notificationRepository;

    public void sendNotification(User user, UserNotification.NotificationType type, String message) {
        UserNotification notification = new UserNotification();
        notification.setUserId(user.getId());
        notification.setNotificationType(type);
        notification.setMessage(message);
        notification.setStatus(UserNotification.NotificationStatus.UNSENT); // Default status
        // Save notification in the database
        notificationRepository.save(notification);
    }
    public void sendNotification_otherRequest(JWTUserDTO userDTO, UserNotification.NotificationType type, String message) {
        UserNotification notification = new UserNotification();
        notification.setUserId(userDTO.getUserId());
        notification.setNotificationType(type);
        notification.setMessage(message);
        notification.setStatus(UserNotification.NotificationStatus.UNSENT); // Default status
        // Save notification in the database
        notificationRepository.save(notification);
    }
}
