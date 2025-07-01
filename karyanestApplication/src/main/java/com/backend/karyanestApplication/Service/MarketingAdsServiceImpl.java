package com.backend.karyanestApplication.Service;

import com.backend.karyanestApplication.Component.extractUrl;
import com.backend.karyanestApplication.Interface.MarketingAdsService;
import com.backend.karyanestApplication.Model.AdPlatforms;
import com.backend.karyanestApplication.Model.MarketingAds;
import com.backend.karyanestApplication.Repositry.AdPlatformRepository;
import com.backend.karyanestApplication.Repositry.MarketingAdsRepository;
import com.backend.karyanestApplication.Repositry.PropertyRepository;
import com.example.Authentication.DTO.JWTUserDTO;
import com.example.module_b.ExceptionAndExceptionHandler.CustomException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MarketingAdsServiceImpl implements MarketingAdsService {
    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private MarketingAdsRepository marketingAdsRepository;

    @Autowired
    private  AdPlatformServiceImpl adPlatformService;
    @Autowired
    private AdPlatformRepository adPlatformRepository;
    @Autowired
    private extractUrl extractUrl;

    @Override
    public MarketingAds createAd(MarketingAds ad, JWTUserDTO jwtUserDTO) {
        String platformName = ad.getPlatform().name();
        ad.setPostedBy(jwtUserDTO.getUserId());
        if (!propertyRepository.existsById(ad.getPropertyId())) {
            throw new CustomException("Property not found with ID: " + ad.getPropertyId(), HttpStatus.BAD_REQUEST);
        }
        boolean exists = adPlatformRepository.existsByPlatformName(platformName);
        if (!exists) {
            String platformUrl = extractUrl.extractPlatformUrlFromAdUrl(ad.getAdUrl()); // Extract from ad URL

            AdPlatforms platform = AdPlatforms.builder()
                    .platformName(platformName)
                    .platformUrl(platformUrl)
                    .build();

            adPlatformRepository.save(platform);
        }

        return marketingAdsRepository.save(ad);
    }

    @Override
    public MarketingAds updateAd(Integer adId, MarketingAds updatedAd, JWTUserDTO jwtUserDTO) {

        MarketingAds existingAd = marketingAdsRepository.findById(adId)
                .orElseThrow(() -> new CustomException("Ad not found with ID: " + adId, HttpStatus.NOT_FOUND));

        if (!propertyRepository.existsById(updatedAd.getPropertyId())) {
            throw new CustomException("Property not found with ID: " + updatedAd.getPropertyId(), HttpStatus.BAD_REQUEST);
        }

        // Update existing ad fields
        existingAd.setPostedBy(jwtUserDTO.getUserId());
        existingAd.setAdTitle(updatedAd.getAdTitle());
        existingAd.setAdDescription(updatedAd.getAdDescription());
        existingAd.setAdStatus(updatedAd.getAdStatus());
        existingAd.setAdCategory(updatedAd.getAdCategory());
        existingAd.setAdUrl(updatedAd.getAdUrl());
        existingAd.setPlatform(updatedAd.getPlatform());
        existingAd.setExpiresOn(updatedAd.getExpiresOn());
        existingAd.setPostedOn(updatedAd.getPostedOn());
        existingAd.setPropertyId(updatedAd.getPropertyId());

        String platformName = updatedAd.getPlatform().name();
        if (!adPlatformRepository.existsByPlatformName(platformName)) {
            String platformUrl = extractUrl.extractPlatformUrlFromAdUrl(updatedAd.getAdUrl()); // Extract from ad URL

            AdPlatforms platform = AdPlatforms.builder()
                    .platformName(platformName)
                    .platformUrl(platformUrl)
                    .build();

            adPlatformRepository.save(platform);
        }

        return marketingAdsRepository.save(existingAd);
    }

    // Helper method to extract platform URL from ad URL
    @Override
    public void deleteAd(Integer adId) {
        if (!marketingAdsRepository.existsById(adId)) {
            throw new CustomException("Ad not found with ID: " + adId, HttpStatus.NOT_FOUND);
        }
        marketingAdsRepository.deleteById(adId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MarketingAds> getAdById(Integer adId) {
        return Optional.ofNullable(marketingAdsRepository.findById(adId)
                .orElseThrow(() -> new CustomException("Ad not found with ID: " + adId, HttpStatus.NOT_FOUND)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarketingAds> getAllAds() {
        List<MarketingAds> ads = marketingAdsRepository.findAll();
        if (ads.isEmpty()) {
            throw new CustomException("No ads found", HttpStatus.NOT_FOUND);
        }
        return ads;
    }

}
