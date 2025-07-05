package com.backend.karyanestApplication.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "conversations")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "initiator_id", nullable = false)
    private Long initiatorId; // Only store User ID, no relation

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId; // Only store User ID, no relation
    @Column(name = "property_id")
    private Long propertyId; // Store Property ID (nullable for non-property chats)
    @Column(name="propertyOwnerId")
    private Long propertyOwnerId;
    @Column(name="assigned_id")
    private Long assignedId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConversationType type; // Type of conversation

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConversationStatus status; // Chat status

    public enum ConversationType {
        GENERAL,
        PROPERTY_INQUIRY,
        SUPPORT
    }

    public enum ConversationStatus {
        OPEN,
        CLOSED
    }
}
