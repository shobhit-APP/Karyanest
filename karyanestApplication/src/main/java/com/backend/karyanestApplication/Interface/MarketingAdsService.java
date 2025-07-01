package com.backend.karyanestApplication.Interface;

import com.backend.karyanestApplication.Model.MarketingAds;
import com.example.Authentication.DTO.JWTUserDTO;

import java.util.List;
import java.util.Optional;

public interface MarketingAdsService {

    MarketingAds createAd(MarketingAds ad, JWTUserDTO jwtUserDTO);

    MarketingAds updateAd(Integer adId, MarketingAds updatedAd, JWTUserDTO jwtUserDTO);

    void deleteAd(Integer adId);
    Optional<MarketingAds> getAdById(Integer adId);
    List<MarketingAds> getAllAds();
}
