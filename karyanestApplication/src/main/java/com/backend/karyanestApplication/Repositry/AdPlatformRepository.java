package com.backend.karyanestApplication.Repositry;

import com.backend.karyanestApplication.Model.AdPlatforms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdPlatformRepository extends JpaRepository<AdPlatforms, Integer> {
    boolean existsByPlatformName(String platformName);
}
