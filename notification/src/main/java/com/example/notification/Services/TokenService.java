package com.example.notification.Services;

import com.example.notification.Model.FCMToken;
import com.example.notification.Repository.FCMTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TokenService {

    @Autowired
    private FCMTokenRepository fcmTokenRepository;

    @Transactional
    public void updateFCMToken(Long userId, String fcmToken, String deviceId) {

//        User user = userRepo.findById(userId).orElseThrow(
//                () -> new RuntimeException("User not found"));

        // Retrieve all existing tokens for the user
        List<FCMToken> existingTokens = fcmTokenRepository.findByUser(userId);
        // List<FCMToken> existingTokensByDeviceId = fcmTokenRepository.findByDeviceId(deviceId);

        // Check if the token already exists
        boolean tokenExists = existingTokens.stream()
                .anyMatch(existingToken -> existingToken.getToken().equals(fcmToken));

        // If not, save the new token and device ID
        if (!tokenExists) {
            FCMToken token = new FCMToken();
            token.setUser(userId);
            token.setToken(fcmToken);
            token.setDeviceId(deviceId);
            fcmTokenRepository.save(token);
        }
    }
}