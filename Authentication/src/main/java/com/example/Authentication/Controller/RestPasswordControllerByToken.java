package com.example.Authentication.Controller;

import com.example.Authentication.Component.UserContext;
import com.example.Authentication.DTO.JWTUserDTO;
import com.example.Authentication.Service.Auth;
import com.example.Authentication.Service.UserHandleService;
import com.example.Authentication.UTIL.JwtUtil;
import com.example.module_b.ExceptionAndExceptionHandler.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/reset/")
public class RestPasswordControllerByToken {
    @Autowired
    private Auth auth;
    @Autowired
    private UserContext userContext;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserHandleService userHandleService;

    /**
     * Complete password reset using token (for email method)
     */
    private static final Logger logger = LoggerFactory.getLogger(RestPasswordControllerByToken.class);
    @PostMapping("/reset-password-using-token")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> requestBody, HttpServletRequest request) {
        String newPassword = requestBody.get("newPassword");
        JWTUserDTO user = (JWTUserDTO) request.getAttribute("user");
        String token = userContext.extractToken(request);

        if (auth.isNullOrEmpty(token) || auth.isNullOrEmpty(newPassword)) {
            logger.warn("Missing required fields: token or newPassword");
            return ResponseEntity.badRequest().body(Map.of("error", "Token and new password are required"));
        }

        try {
            logger.info("Starting password reset for token: {}", token);
            logger.debug("User details: {}", user);

            boolean isValid = jwtUtil.validateToken(token, user.getUsername(), user.getUserRole());
            logger.debug("Token validation result: {}", isValid);
            if (!isValid) {
                logger.warn("Invalid or expired token provided: {}", token);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            logger.debug("Attempting to update password for user ID: {}", user.getUserId());

            userHandleService.updatePassword(user.getUserId(), newPassword);
            logger.info("Password successfully updated for user ID: {}", user.getUserId());

            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));

        }  catch (CustomException e) {
            logger.error("Password validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid user error for token: {}, error: {}", token, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid user: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Password reset error for token: {}, error: {}", token, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Password reset failed: " + e.getMessage()));
        }
    }
}