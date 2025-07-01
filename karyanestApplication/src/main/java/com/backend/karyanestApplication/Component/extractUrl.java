package com.backend.karyanestApplication.Component;

import org.springframework.stereotype.Component;

@Component
public class extractUrl {
    public String extractPlatformUrlFromAdUrl(String adUrl) {
        if (adUrl == null || adUrl.isEmpty()) {
            return "";
        }

        try {
            // Extract base URL (protocol + domain)
            // For example: https://magicbricks.com/ads/2bhk123 -> https://magicbricks.com
            String[] parts = adUrl.split("/");
            if (parts.length >= 3) {
                return parts[0] + "//" + parts[2]; // protocol + domain
            }
            return adUrl; // fallback to original URL if parsing fails
        } catch (Exception e) {
            return adUrl; // fallback to original URL if any error occurs
        }
    }
}
