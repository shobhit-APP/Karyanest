package com.example.notification.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "fcm_tokens")
@Data
@NoArgsConstructor
public class FCMToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    private Long user;

    @Column(name = "token", nullable = false, length = 255)
    private String token;

    @Column(name = "device_id", nullable = false, length = 255)
    private String deviceId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;
}
