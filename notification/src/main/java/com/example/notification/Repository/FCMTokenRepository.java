package com.example.notification.Repository;

import com.example.notification.Model.FCMToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FCMTokenRepository extends JpaRepository<FCMToken, Long> {
    List<FCMToken> findByUser(Long userId);
    //List<FCMToken> findByDeviceId(String deviceId);
}