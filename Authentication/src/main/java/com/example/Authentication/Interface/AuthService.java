package com.example.Authentication.Interface;
import com.example.Authentication.DTO.UserDTO;
import com.example.Authentication.Service.Auth;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    String loginWithEmail(String email);
    String loginWithPhone(String PhoneNumber);
    ResponseEntity<?> loginWithPhoneAndOtp(String Phone);
    boolean Check(String phoneNumber, String username, String email, String password);
    UserDTO findUser(Auth.LoginMethod loginMethod, String username, String PhoneNumber, String email);
}


