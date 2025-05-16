package com.backend.karyanestApplication.Repositry;

import com.backend.karyanestApplication.Model.PropertyFavorite;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyFavoriteRepository extends JpaRepository<PropertyFavorite, Long> {

    List<PropertyFavorite> findByUserId(Long userId);
    List<PropertyFavorite> findByPropertyId(Long propertyId);
    boolean existsByUserIdAndPropertyId(Long userId, Long propertyId);

    @Modifying
    @Transactional
    @Query("DELETE FROM PropertyFavorite pf WHERE pf.user.id = :userId AND pf.property.id = :propertyId")
    int deleteByUserIdAndPropertyId(Long userId, Long propertyId);
}