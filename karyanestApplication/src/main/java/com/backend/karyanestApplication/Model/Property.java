package com.backend.karyanestApplication.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "property_type", nullable = false)
    private PropertyType propertyType;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "listing_type")
    private ListingType listingType;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(length = 10)
    private String currency = "INR";

    @Column(name = "area_size", nullable = false, precision = 10, scale = 2)
    private BigDecimal areaSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "area_unit", nullable = false)
    private AreaUnit areaUnit;

    private Integer bedrooms;

    private Integer bathrooms;

    private Integer balconies;

    @Enumerated(EnumType.STRING)
    private FurnishedStatus furnishedStatus;

    private Integer parkingSpaces = 0;

    private Integer floorNumber;

    private Integer totalFloors;

    @Enumerated(EnumType.STRING)
    private OwnershipType ownershipType;

    private String ageOfProperty;

    @Enumerated(EnumType.STRING)
    private ConstructionStatus constructionStatus;

    @Enumerated(EnumType.STRING)
    private FacingDirection facingDirection;

    private BigDecimal roadWidth;

    private Boolean waterAvailability = true;

    private Boolean electricityAvailability = true;

    @Column(columnDefinition = "TEXT")
    private String securityFeatures;

    @Column(columnDefinition = "TEXT")
    private String nearbyLandmarks;

    @Column(nullable = false)
    private String locationAddress;

    private String city;

    private String state;

    private String country;

    private String pincode;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String videoUrl;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Lead> leads;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
    private List<PropertyPriceChange> priceChanges = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        this.updatedAt = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
    }

    public enum PropertyType {
        FLAT, PLOT, LAND, HOUSE, VILLA, APARTMENT
    }

    public enum Status {
        DRAFT, AVAILABLE, SOLD, PENDING, RENTED, DELETED
    }

    public enum ListingType {
        FOR_SALE, FOR_RENT, LEASE
    }

    public enum AreaUnit {
        SQ_FT, SQ_YARDS, ACRES, HECTARES, DISMIL
    }

    public enum FurnishedStatus {
        FURNISHED, SEMI_FURNISHED, UNFURNISHED
    }

    public enum OwnershipType {
        FREEHOLD, LEASEHOLD, COOPERATIVE
    }

    public enum ConstructionStatus {
        UNDER_CONSTRUCTION, READY_TO_MOVE
    }

    public enum FacingDirection {
        NORTH, SOUTH, EAST, WEST, NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST
    }

    public enum VerificationStatus {
        PENDING, VERIFIED, REJECTED
    }
}