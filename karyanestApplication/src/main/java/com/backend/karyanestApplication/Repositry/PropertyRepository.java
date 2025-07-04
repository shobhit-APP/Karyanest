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
//    @Query(value = """
//            SELECT DISTINCT p.* FROM properties p
//            LEFT JOIN Amenities a ON p.id = a.property_id
//            WHERE ((:minPrice IS NULL AND :maxPrice IS NULL) OR
//                   (:minPrice IS NOT NULL AND :maxPrice IS NOT NULL AND p.price BETWEEN :minPrice AND :maxPrice))
//              AND (:propertyType IS NULL OR p.property_type = :propertyType)
//              AND (:listingType IS NULL OR p.listing_type = :listingType)
//              AND (:locationAddress IS NULL OR LOWER(p.locationAddress) LIKE CONCAT('%', LOWER(:locationAddress), '%'))
//              AND (:amenity IS NULL OR LOWER(a.amenities) LIKE CONCAT('%', LOWER(:amenity), '%'))
//              AND (:bedrooms IS NULL OR :bedrooms = 0 OR p.bedrooms = :bedrooms)
//              AND (:bathrooms IS NULL OR :bathrooms = 0 OR p.bathrooms = :bathrooms)
//            """, nativeQuery = true)
//    List<Property> searchProperties(
//            @Param("minPrice") BigDecimal minPrice,
//            @Param("maxPrice") BigDecimal maxPrice,
//            @Param("propertyType") String propertyType,
//            @Param("listingType") String listingType,
//            @Param("locationAddress") String locationAddress,
//            @Param("amenity") String amenity,
//            @Param("bedrooms") Integer bedrooms,
//            @Param("bathrooms") Integer bathrooms
//    );
//}
    @Query(value = """
               SELECT DISTINCT p.* FROM properties p
               LEFT JOIN Amenities a ON p.id = a.property_id
               WHERE (
                   (:minPrice IS NULL OR p.price >= :minPrice)
                   AND (:maxPrice IS NULL OR p.price <= :maxPrice)
               )
               AND (
                   (:propertyType IS NULL OR p.property_type = :propertyType)
                   OR (:listingType IS NULL OR p.listing_type = :listingType)
                   OR (:locationAddress IS NULL OR LOWER(p.locationAddress) LIKE CONCAT('%', LOWER(:locationAddress), '%'))
                   OR (:amenity IS NULL OR LOWER(a.amenities) LIKE CONCAT('%', LOWER(:amenity), '%'))
                   OR (:bedrooms IS NULL OR :bedrooms = 0 OR p.bedrooms = :bedrooms)
                   OR (:bathrooms IS NULL OR :bathrooms = 0 OR p.bathrooms = :bathrooms)
               )
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
