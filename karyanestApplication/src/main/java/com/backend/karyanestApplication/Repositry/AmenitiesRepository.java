package com.backend.karyanestApplication.Repositry;

import com.backend.karyanestApplication.DTO.AmenitiesResponseDTO;
import com.backend.karyanestApplication.Model.Amenities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmenitiesRepository extends JpaRepository<Amenities, Long> {
//    Amenities findByPropertyId(Long id);
    List<Amenities> findByPropertyId(Long id);
    void deleteByPropertyId(Long id);
    // Add custom query methods here if needed
}