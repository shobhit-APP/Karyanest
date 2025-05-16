package com.backend.karyanestApplication.DTO;

import com.backend.karyanestApplication.Model.Property;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PropertyCreateDTO {
    private Long draftId;
    // Page 1: Property Info
    private String title;
    private Property.PropertyType propertyType;
    private String locationAddress;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private BigDecimal price;
    private BigDecimal areaSize;
    private Property.AreaUnit areaUnit;
    private Integer bedrooms;
    private Integer bathrooms;

    // Page 2: Property Details
    private String description;
    private String ageOfProperty;
    private Integer floorNumber;
    private Integer totalFloors;
    private Property.FurnishedStatus furnishedStatus;
    private Property.FacingDirection facingDirection;
    private String amenities;
    private String nearbyLandmarks;

    // Page 3: Legal & Compliance
    private Property.OwnershipType ownershipType;
    private Property.ConstructionStatus constructionStatus;
}
