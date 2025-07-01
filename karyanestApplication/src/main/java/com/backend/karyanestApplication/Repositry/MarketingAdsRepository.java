package com.backend.karyanestApplication.Repositry;

import com.backend.karyanestApplication.Model.MarketingAds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketingAdsRepository extends JpaRepository<MarketingAds, Integer> {
    // Custom query methods can go here
}
