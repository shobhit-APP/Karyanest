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
//    void deleteByStatusAndCreatedAtBefore(Property.Status status, LocalDateTime threshold);

    @Query("SELECT p FROM Property p WHERE " +
            "(:locationAddress IS NULL OR LOWER(p.locationAddress) LIKE LOWER(CONCAT('%', :locationAddress, '%'))) " +
            "AND (:propertyType IS NULL OR p.propertyType = :propertyType) " +
            "AND (:listingType IS NULL OR p.listingType = :listingType) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
            "AND (:bedrooms IS NULL OR p.bedrooms = :bedrooms) " +
            "AND (:bathrooms IS NULL OR p.bathrooms = :bathrooms) " +
            "AND (:amenities IS NULL OR LOWER(p.amenities) LIKE LOWER(CONCAT('%', :amenities, '%')))")
    List<Property> searchProperties(@Param("locationAddress") String locationAddress,
                                    @Param("propertyType") Property.PropertyType propertyType,
                                    @Param("listingType") Property.ListingType listingType,
                                    @Param("minPrice") BigDecimal minPrice,
                                    @Param("maxPrice") BigDecimal maxPrice,
                                    @Param("bedrooms") Integer bedrooms,
                                    @Param("bathrooms") Integer bathrooms,
                                    @Param("amenities") String amenities);

}
