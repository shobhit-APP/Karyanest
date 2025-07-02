package com.backend.karyanestApplication.DTO;

import com.backend.karyanestApplication.Model.Property;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyDTO {

    private Long id;
    private String title;
    private String description;
    private Property.PropertyType propertyType;
    private Property.Status status;
    private Property.ListingType listingType;
    private BigDecimal price;
    private String currency;
    private BigDecimal areaSize;
    private Property.AreaUnit areaUnit;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer balconies;
    private Property.FurnishedStatus furnishedStatus;
    private Integer parkingSpaces;
    private Integer floorNumber;
    private Integer totalFloors;
    private Property.OwnershipType ownershipType;
    private String ageOfProperty;
    private Property.ConstructionStatus constructionStatus;
    private Property.FacingDirection facingDirection;
    private BigDecimal roadWidth;
    private Boolean waterAvailability;
    private Boolean electricityAvailability;
    private Boolean internetAvailability;
    private Boolean gasAvailability;
    private Boolean sewageAvailability;
    private Boolean publicTransportAvailable;
    private Boolean highFootTraffic;
    private String securityFeatures;
    private String nearbyLandmarks;
    private String locationAddress;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal availableLeaseArea;
    private String floorPlanUrl;
    private String streetFrontage;
    private String zoningClassification;
    private List<String> accessibilityFeatures;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kolkata")
    private ZonedDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kolkata")
    private ZonedDateTime updateAt;

    private Property.VerificationStatus verificationStatus;
    private String amenities;
    private List<PropertyResourceDTO> propertyResources;
    private List<AmenitiesResponseDTO> amenitiesResponseDTOS;

    private Map<String, Object> extraFields = new HashMap<>();

    public PropertyDTO(Property property) {
        this.id = property.getId();
        this.title = property.getTitle();
        this.description = property.getDescription();
        this.propertyType = property.getPropertyType();
        this.status = property.getStatus();
        this.listingType = property.getListingType();
        this.price = property.getPrice();
        this.currency = property.getCurrency();
        this.areaSize = property.getAreaSize();
        this.areaUnit = property.getAreaUnit();
        this.bedrooms = property.getBedrooms();
        this.bathrooms = property.getBathrooms();
        this.balconies = property.getBalconies();
        this.furnishedStatus = property.getFurnishedStatus();
        this.parkingSpaces = property.getParkingSpaces();
        this.floorNumber = property.getFloorNumber();
        this.totalFloors = property.getTotalFloors();
        this.ownershipType = property.getOwnershipType();
        this.ageOfProperty = property.getAgeOfProperty();
        this.constructionStatus = property.getConstructionStatus();
        this.facingDirection = property.getFacingDirection();
        this.roadWidth = property.getRoadWidth();
        this.waterAvailability = property.getWaterAvailability();
        this.electricityAvailability = property.getElectricityAvailability();
        this.internetAvailability = property.getInternetAvailability();
        this.gasAvailability = property.getGasAvailability();
        this.sewageAvailability = property.getSewageAvailability();
        this.publicTransportAvailable = property.getPublicTransportAvailable();
        this.highFootTraffic = property.getHighFootTraffic();
        this.securityFeatures = property.getSecurityFeatures();
        this.nearbyLandmarks = property.getNearbyLandmarks();
        this.locationAddress = property.getLocationAddress();
        this.city = property.getCity();
        this.state = property.getState();
        this.country = property.getCountry();
        this.pincode = property.getPincode();
        this.latitude = property.getLatitude();
        this.longitude = property.getLongitude();
        this.availableLeaseArea = property.getAvailableLeaseArea();
        this.floorPlanUrl = property.getFloorPlanUrl();
        this.streetFrontage = property.getStreetFrontage();
        this.zoningClassification = property.getZoningClassification();
        this.accessibilityFeatures = property.getAccessibilityFeatures();
        this.createdAt = property.getCreatedAt();
        this.updateAt = property.getUpdatedAt();
        this.verificationStatus = property.getVerificationStatus();
        this.propertyResources = new ArrayList<>();
        this.amenitiesResponseDTOS = new ArrayList<>();
        // Note: amenities field will be set by PropertyService
    }

    @JsonAnySetter
    public void set(String key, Object value) {
        extraFields.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getExtraFields() {
        return extraFields;
    }
}
