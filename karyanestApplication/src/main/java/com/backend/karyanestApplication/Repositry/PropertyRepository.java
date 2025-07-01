package com.backend.karyanestApplication.Repositry;

import com.backend.karyanestApplication.Model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByUserId(Long userId);

    Property getPropertyById(Long propertyId);

    //    List<Property> findByStatusAndUserId(Property.Status status, Long userId);
//    void deleteByStatusAndCreatedAtBefore(Property.Status status, LocalDateTime threshold)
//    ;
    @Query(value = """
            SELECT * FROM properties
            WHERE ((:minPrice IS NULL AND :maxPrice IS NULL) OR
            (:minPrice IS NOT NULL AND :maxPrice IS NOT NULL AND price BETWEEN :minPrice AND :maxPrice))
              AND (:propertyType IS NULL OR property_type = :propertyType OR property_type IS NULL)
              AND (:listingType IS NULL OR listing_type = :listingType OR listing_type IS NULL)
              AND (:locationAddress IS NULL OR LOWER(locationAddress) LIKE CONCAT('%', LOWER(:locationAddress), '%') OR locationAddress IS NULL)
              AND (:amenity IS NULL OR LOWER(amenities) LIKE CONCAT('%', LOWER(:amenity), '%') OR amenities IS NULL)
              AND (:bedrooms IS NULL OR :bedrooms = 0 OR bedrooms = :bedrooms OR bedrooms IS NULL)
              AND (:bathrooms IS NULL OR :bathrooms = 0 OR bathrooms = :bathrooms OR bathrooms IS NULL)
            """, nativeQuery = true)
    List<Property> searchProperties(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("propertyType") String propertyType,
            @Param("listingType") String listingType,
            @Param("locationAddress") String locationAddress,
            @Param("amenity") String amenity,
            @Param("bedrooms") Integer bedrooms,
            @Param("bathrooms") Integer bathrooms
    );
}
