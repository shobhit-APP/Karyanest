package com.backend.karyanestApplication.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.UUID;

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
    private Timestamp visitTime;

    @Column(name = "device_info", nullable = true)
    private String deviceInfo;

    @Column(name = "location_coords", nullable = true)
    private String locationCoords;

    
    public UserPropertyVisit(Long userId, Long id, String deviceInfo, Timestamp from) {
        this.userId=userId;
        this.deviceInfo=deviceInfo;
        this.propertyId=id;
        this.visitTime=from;
    }
}



