
package com.backend.karyanestApplication.Repositry;

import com.backend.karyanestApplication.Model.PropertyPriceChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyPriceChangeRepository extends JpaRepository<PropertyPriceChange, Long> {

    // Find all price changes for a specific property
    List<PropertyPriceChange> findByPropertyIdOrderByCreatedAtDesc(Long propertyId);

    // Find price changes within a date range
    List<PropertyPriceChange> findByPropertyIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long propertyId,
            java.sql.Timestamp startDate,
            java.sql.Timestamp endDate);

    // Find the most recent price change for a property
    PropertyPriceChange findTopByPropertyIdOrderByCreatedAtDesc(Long propertyId);

    // Count how many price changes a property has had
    long countByPropertyId(Long propertyId);
    Optional<PropertyPriceChange> findByPropertyId(Long id);
}