package com.backend.karyanestApplication.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.ZonedDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "property_resources")
@Getter
@Setter
public class PropertyResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "property_id", nullable = false)
    private Long propertyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false)
    private ResourceType resourceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "title")
    private ResourceTitle title;

    @Column(name = "url", length = 255, nullable = false)
    private String url;

    @Column(name = "file_id")
    private String fileId;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    private Long fileSize;
    private String fileType;
    private Integer sortOrder;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "update_at")
    private ZonedDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        this.updatedAt = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
    }

    public enum ResourceType {
        Image, Video,Document
    }

    public enum ResourceTitle {
        Front, Interior, Floorplan, Aerial
    }
}