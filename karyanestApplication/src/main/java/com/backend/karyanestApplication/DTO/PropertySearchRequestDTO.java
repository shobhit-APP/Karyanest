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
    public boolean isEmpty() {
        return (minPrice == null && maxPrice == null && propertyType == null &&
                listingType == null && (locationAddress == null || locationAddress.isBlank()) &&
                (amenities == null || amenities.isEmpty()) && bedrooms == 0 && bathrooms == 0);
    }
}
