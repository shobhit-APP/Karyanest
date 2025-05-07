
package com.backend.karyanestApplication.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Property ID (Primary Key)

    @Column(nullable = false)
    private String title; // Property listing title

    @Column(columnDefinition = "TEXT")
    private String description; // Property description

    @Enumerated(EnumType.STRING)
    @Column(name = "property_type", nullable = false)
    private PropertyType propertyType; // Flat, Plot, Land, House

    @Enumerated(EnumType.STRING)
    private Status status; // Available, Sold, Pending, Rented

    @Enumerated(EnumType.STRING)
    @Column(name = "listing_type")
    private ListingType listingType; // For Sale, For Rent, Lease

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price; // Price of the property

    @Column(length = 10)
    private String currency = "INR"; // Currency type

    @Column(name = "area_size", nullable = false, precision = 10, scale = 2)
    private BigDecimal areaSize; // Property size

    @Enumerated(EnumType.STRING)
    @Column(name = "area_unit", nullable = false)
    private AreaUnit areaUnit; // Sq. Ft, Sq. Yards, Acres, etc.

    @Column()
    private Integer bedrooms;

    @Column()
    private Integer bathrooms;

    @Column()
    private Integer balconies;

    @Enumerated(EnumType.STRING)
    private FurnishedStatus furnishedStatus; // Furnished, Semi-Furnished, Unfurnished

    private Integer parkingSpaces = 0; // Parking slots

    private Integer floorNumber; // Floor number (for Flats)

    private Integer totalFloors; // Total floors in the building

    @Enumerated(EnumType.STRING)
    @Column()
    private OwnershipType ownershipType; // Freehold, Leasehold, Cooperative

    private String ageOfProperty; // Property age (e.g., "5 years")

    @Enumerated(EnumType.STRING)
    @Column()
    private ConstructionStatus constructionStatus; // Under Construction, Ready to Move

    @Enumerated(EnumType.STRING)
    private FacingDirection facingDirection; // Property facing direction

    private BigDecimal roadWidth; // Width of the road in front

    private Boolean waterAvailability = true; // Water availability

    private Boolean electricityAvailability = true; // Electricity availability

    private String securityFeatures; // Security features (e.g., CCTV)

    @Column(columnDefinition = "TEXT")
    private String amenities; // List of amenities

    @Column(columnDefinition = "TEXT")
    private String nearbyLandmarks; // Important landmarks nearby

    @Column(nullable = false)
    private String locationAddress; // Full address

//    @Column(nullable = false, length = 100)
    private String city; // City name

//    @Column(nullable = false, length = 100)
    private String state; // State name
//
//    @Column(nullable = false, length = 100)
    private String country;

//    @Column(nullable = false, length = 10)
    private String pincode; // Postal code

    private BigDecimal latitude; // Latitude for location

    private BigDecimal longitude; // Longitude for location


    private String videoUrl; // Video tour URL (if available)


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Reference to user

    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus; // Pending, Verified, Rejected

    @Column(name = "created_at")
    private LocalDateTime createdAt; // Timestamp when added

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // Timestamp when last updated

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Lead> leads; // Leads related to the property
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
    private List<PropertyPriceChange> priceChanges = new ArrayList<>();

    // Enums
    public enum PropertyType {
        FLAT, PLOT, LAND, HOUSE,VILLA,APARTMENT
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

