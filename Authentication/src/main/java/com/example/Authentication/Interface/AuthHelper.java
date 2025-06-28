package com.example.Authentication.Interface;

import com.example.Authentication.DTO.UserDTO;
import com.example.Authentication.Service.Auth;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface AuthHelper {
    Auth.LoginMethod determineLoginMethod(String Username, String email, String PhoneNumber);
    String generateResetLink(HttpServletRequest request);
    String maskEmail(String email);
    UserDTO authenticateUser(Auth.LoginMethod method, String username, String email, String phoneNumber, String password);
    boolean isNullOrEmpty(String str);
    ResponseEntity<?> handelPhoneVerification(String PhoneNumber, String verificationUrl);
    String maskPhoneNumber(String phoneNumber);
    ResponseEntity<?> handleLoginRequest(String username, String phoneNumber, String email, String password);

}
