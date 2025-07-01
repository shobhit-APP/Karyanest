package com.backend.karyanestApplication.Controller;

import com.backend.karyanestApplication.Model.AdPlatforms;
import com.backend.karyanestApplication.Model.MarketingAds;
import com.backend.karyanestApplication.Service.AdPlatformServiceImpl;
import com.backend.karyanestApplication.Service.MarketingAdsServiceImpl;
import com.example.Authentication.DTO.JWTUserDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/ads")
public class MarketingAdsController {

    @Autowired
    private MarketingAdsServiceImpl marketingAdsService;
    @Autowired
    private AdPlatformServiceImpl adPlatformService;

    // ✅ CREATE
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('create_ad')")
    public ResponseEntity<MarketingAds> createAd(@RequestBody MarketingAds ad, HttpServletRequest httpServletRequest) {
        JWTUserDTO userDTO= (JWTUserDTO) httpServletRequest.getAttribute("user");
        MarketingAds createdAd = marketingAdsService.createAd(ad,userDTO);
        return ResponseEntity.status(201).body(createdAd);
    }

    // ✅ UPDATE
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('update_ad')")
    public ResponseEntity<MarketingAds> updateAd(@PathVariable Integer id, @RequestBody MarketingAds ad,HttpServletRequest request) {
        JWTUserDTO userDTO= (JWTUserDTO) request.getAttribute("user");
        return ResponseEntity.ok(marketingAdsService.updateAd(id, ad,userDTO));
    }

    // ✅ GET BY ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('get_ad_by_id')")
    public ResponseEntity<Optional<MarketingAds>> getAdById(@PathVariable Integer id) {
        return ResponseEntity.ok(marketingAdsService.getAdById(id));
    }

    // ✅ GET ALL
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('get_all_ads')")
    public ResponseEntity<List<MarketingAds>> getAllAds() {
        return ResponseEntity.ok(marketingAdsService.getAllAds());
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('delete_ad')")
    public ResponseEntity<Void> deleteAd(@PathVariable Integer id) {
        marketingAdsService.deleteAd(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/all_platforms")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('get_all_platform')")
    public ResponseEntity<List<AdPlatforms>> getAllPlatforms() {
        return ResponseEntity.ok(adPlatformService.getAllPlatforms());
    }
}
