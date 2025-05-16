package com.backend.karyanestApplication.DTO;

import com.backend.karyanestApplication.Model.Property;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertySearchRequestDTO {
    private String locationAddress;
    private Property.PropertyType propertyType;
    private Property.ListingType listingType;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer bedrooms;
    private Integer bathrooms;
    private String amenities;
}
