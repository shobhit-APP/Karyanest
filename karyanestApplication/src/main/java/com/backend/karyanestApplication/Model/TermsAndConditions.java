package com.backend.karyanestApplication.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.ZonedDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "terms_and_conditions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TermsAndConditions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 50)
    private String version;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('ACTIVE', 'IN_ACTIVE') DEFAULT 'ACTIVE'")
    private Status status = Status.ACTIVE;

    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        updatedAt = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
    }

    public enum Status {
        ACTIVE,
        IN_ACTIVE
    }
}