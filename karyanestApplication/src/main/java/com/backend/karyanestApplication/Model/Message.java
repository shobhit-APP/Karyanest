package com.backend.karyanestApplication.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation; // Chat session

    @Column(name = "sender_id", nullable = false)
    private Long senderId; // Store User ID, no direct relation

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message; // Message content

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now(); // When the message was sent
}
