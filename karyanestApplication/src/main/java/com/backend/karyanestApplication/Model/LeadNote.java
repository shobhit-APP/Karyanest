package com.backend.karyanestApplication.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.ZonedDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "lead_notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class LeadNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noteId;

    @ManyToOne
    @JoinColumn(name = "leadId", nullable = false)
    private Lead lead;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String note;

    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updatedAt", nullable = false)
    private ZonedDateTime updatedAt;

    @Column(name = "noteadded_by_id")
    private Long agentId;

    @Column(name = "noteadded_by")
    private String agentName;

    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        this.updatedAt = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
    }
}