package com.example.Authentication.Service;

public interface OtpService {
    String generateAndStoreOtp(String phoneNumber);
    boolean verifyOtp(String phoneNumber, String otp);
}