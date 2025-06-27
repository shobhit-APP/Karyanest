package com.backend.karyanestApplication.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "user_property_visits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPropertyVisit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "property_id", nullable = false)
    private Long propertyId;

    @Column(name = "visit_time", nullable = false)
    private ZonedDateTime visitTime;

    @Column(name = "device_info", nullable = true)
    private String deviceInfo;

    @Column(name = "location_coords", nullable = true)
    private String locationCoords;

    @PrePersist
    protected void onCreate() {
        if (this.visitTime == null) {
            this.visitTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        }
    }

    public UserPropertyVisit(Long userId, Long propertyId, String deviceInfo, ZonedDateTime visitTime) {
        this.userId = userId;
        this.propertyId = propertyId;
        this.deviceInfo = deviceInfo;
        this.visitTime = visitTime;
    }
}