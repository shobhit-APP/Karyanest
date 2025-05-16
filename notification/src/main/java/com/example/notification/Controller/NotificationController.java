package com.example.notification.Controller;

import com.example.notification.DTO.NotificationRequestDTO;
import com.example.notification.DTO.NotificationResponse;
import com.example.notification.Services.NotificationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    @Autowired
    private NotificationsService notificationsService;

    @PostMapping("/create")
    public ResponseEntity<NotificationResponse> createNotification(@RequestBody NotificationRequestDTO request) {
        NotificationResponse response = notificationsService.createNotification(request);
        return ResponseEntity
                .created(java.net.URI.create("/v1/notifications/" + response.getId()))
                .body(response);
    }
}
