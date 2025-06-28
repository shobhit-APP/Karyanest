package com.example.Authentication.Interface;

public interface OtpService {
    String generateAndStoreOtp(String phoneNumber);

    boolean verifyOtp(String phoneNumber, String otp);

    void deleteOtpByPhoneNumber(String phoneNumber);
}

