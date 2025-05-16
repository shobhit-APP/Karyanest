package com.backend.karyanestApplication.Repositry;

import com.backend.karyanestApplication.Model.UserPropertyVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserPropertyVisitRepository extends JpaRepository<UserPropertyVisit, Long> {
    
    List<UserPropertyVisit> findByUserId(Long userId);
    
    List<UserPropertyVisit> findByPropertyId(Long propertyId);
    
    Optional<UserPropertyVisit> findByUserIdAndPropertyId(Long userId, Long propertyId);
}