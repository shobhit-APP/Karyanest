package com.backend.karyanestApplication.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    @JoinColumn(name = "leadId", nullable = false)  // Fixed column name
    private Lead lead;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String note;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "noteadded_by_id")
    private Long agentId;

    @Column(name = "noteadded_by")
    private String agentName;



    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();  // Initialize updatedAt for new records
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();  // Update timestamp on modification
    }

}
