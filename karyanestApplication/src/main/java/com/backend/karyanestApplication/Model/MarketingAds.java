package com.backend.karyanestApplication.Model;

import com.backend.karyanestApplication.Component.CustomZonedDateTimeDeserializer;
import com.backend.karyanestApplication.Component.CustomZonedDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "marketing_ads")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketingAds {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer adId;

    private Long propertyId;

    @Enumerated(EnumType.STRING)
    private Platform platform;

    @Column(length = 255)
    private String adTitle;

    @Lob
    private String adDescription;

    @Enumerated(EnumType.STRING)
    private AdStatus adStatus;

    private String adCategory;
    private String adUrl;

    private Long postedBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kolkata")
    @JsonDeserialize(using = CustomZonedDateTimeDeserializer.class)
    @JsonSerialize(using = CustomZonedDateTimeSerializer.class)
    private ZonedDateTime postedOn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kolkata")
    @JsonDeserialize(using = CustomZonedDateTimeDeserializer.class)
    @JsonSerialize(using = CustomZonedDateTimeSerializer.class)
    private ZonedDateTime expiresOn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kolkata")
    @JsonDeserialize(using = CustomZonedDateTimeDeserializer.class)
    @JsonSerialize(using = CustomZonedDateTimeSerializer.class)
    private ZonedDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kolkata")
    @JsonDeserialize(using = CustomZonedDateTimeDeserializer.class)
    @JsonSerialize(using = CustomZonedDateTimeSerializer.class)
    private ZonedDateTime updatedAt;

    public enum Platform {
        MAGICBRICKS, OLX, NINETY_NINE_ACRES
    }

    public enum AdStatus {
        ACTIVE, PAUSED, EXPIRED, REMOVED
    }

    @PrePersist
    public void prePersist() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
    }
}
