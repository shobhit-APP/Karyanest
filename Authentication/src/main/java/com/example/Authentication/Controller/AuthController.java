
package com.example.Authentication.Controller;

import com.example.Authentication.DTO.UpdateUserInternalDTO;
import com.example.Authentication.DTO.UserDTO;
import com.example.module_b.ExceptionAndExceptionHandler.CustomException;

import com.example.Authentication.Component.UserContext;
import com.example.Authentication.Service.EmailService;
//import com.example.Authentication.Service.JwtService;
import com.example.Authentication.Service.ReferenceTokenService;
import com.example.Authentication.UTIL.JwtUtil;
import com.example.Authentication.Service.Auth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Objects;


/**
 * Controller for Authentication operations.
 */
@RestController
@RequestMapping("/v1/auth")
@Tag(name = "Authentication", description = "Authentication operations")
public class AuthController {
    private final JwtUtil jwtUtil;
    private final ReferenceTokenService referenceTokenService;
    @Autowired
    private UserContext userContext;
    @Autowired
    private EmailService emailService;
    @Autowired
    private Auth auth;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    public AuthController(JwtUtil jwtUtil, ReferenceTokenService referenceTokenService,
                          AuthenticationManager authenticationManager) {
        this.jwtUtil = jwtUtil;
        this.referenceTokenService = referenceTokenService;
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String phoneNumber = request.get("phoneNumber");
        String email = request.get("email");
        String password = request.get("password");

        try {
            return auth.handleLoginRequest(username, phoneNumber, email, password);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        } catch (Exception e) {
            logger.error("Login error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Authentication failed"));
        }
    }
    /**
     * Verifies a user's email using a token.
     *
     * @param email The email of the user to verify
     * @param token The verification token
     * @return ResponseEntity with a success or error message
     */
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyUser(@RequestParam String email, @RequestParam String token) {
        UserDTO user = auth.getUserDetails("email",email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found with this email."));
        }

        try {
            // Validate token
            auth.verifyPasswordResetToken(token);

            if (Objects.equals(user.getVerificationStatus(), "Verified")) {
                return ResponseEntity.ok(Map.of("message", "Your account is already verified."));
            }

            // Verify the user
            auth.verifyUser(email);
            return ResponseEntity.ok(Map.of("message", "User verified successfully via Email Service."));

        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Verification error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Verification failed. Please try again later."));
        }
    }
        /**
     * Verify a user's phone number during the registration process.
     *
     * @param request A map containing phoneNumber and OTP.
     * @return ResponseEntity with a verification message.
     */
    @Operation(summary = "Verify user phone", description = "Verify a user's phone number during registration")
    @PostMapping("/verify-user-otp")
    public ResponseEntity<Map<String, Object>> verifyRegistrationOtp(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String otpEntered = request.get("otp");

        if (phoneNumber == null || phoneNumber.trim().isEmpty() ||
                otpEntered == null || otpEntered.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Phone number and OTP are required.")
            );
        }

        try {
            boolean isVerified = auth.verifyRegistrationOtp(phoneNumber, otpEntered);

            if (!isVerified) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        Map.of("error", "Invalid OTP or phone number.")
                );
            }

            return ResponseEntity.ok(
                    Map.of("message", "User verified successfully using OTP service.")
            );

        } catch (Exception e) {
            logger.error("OTP verification failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Something went wrong during OTP verification. Please try again later.")
            );
        }
    }
    /**
     * Verify a user's phone number and OTP during login.
     *
     * @param request The request map containing phoneNumber and OTP.
     * @return ResponseEntity with a verification message or authentication response.
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String otpEntered = request.get("otp");

        if (phoneNumber == null || phoneNumber.trim().isEmpty() ||
                otpEntered == null || otpEntered.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Phone number and OTP are required.")
            );
        }

        try {
            // Verify OTP using the service
            boolean isOtpValid = auth.verifyLoginOtp(phoneNumber.trim(), otpEntered.trim());

            if (!isOtpValid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        Map.of("error", "Invalid OTP.")
                );
            }

            UserDTO user = auth.getUserDetails("phoneNumber",phoneNumber.trim());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of("error", "User not found for this phone number.")
                );
            }

            return auth.generateAuthResponseForUser(user);

        } catch (Exception e) {
            logger.error("OTP verification failed during login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "An error occurred during OTP verification. Please try again.")
            );
        }
    }

    /**
     * Validate a reference token.
     *
     * @param referenceToken The reference token to be validated
     * @return ResponseEntity with JWT token
     */
    @Operation(summary = "Validate reference token", description = "Validate a reference token and return the associated JWT token")
    @GetMapping("/validateReferenceToken")
    public ResponseEntity<Map<String, String>> validateReferenceToken(@RequestParam String referenceToken) {
        String jwt = referenceTokenService.getJwtFromReferenceToken(referenceToken);
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or expired reference token. Please log in again."));
        }
        return ResponseEntity.ok(Map.of("jwtToken", jwt));
    }

    /**
     * Logout a user.
     *
     * @param request Map containing the reference token
     * @return ResponseEntity with logout message
     */
    @Operation(summary = "User logout", description = "Invalidate a reference token and logout the user")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestBody Map<String, String> request) {
        String referenceToken = request.get("referenceToken");
        if (referenceToken == null || referenceToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Reference token is required"));
        }

        // Get the username from the reference token
        String jwt = referenceTokenService.getJwtFromReferenceToken(referenceToken);
        String username = jwtUtil.extractUsername(jwt);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid reference token"));
        }
        // Get the user and update status to inactive
        UserDTO user = auth.getUserDetails("username",username);
        if (user != null) {
            UpdateUserInternalDTO updateUserInternalDTO =new UpdateUserInternalDTO();
            updateUserInternalDTO.setStatus("Inactive");
            updateUserInternalDTO.setUserId(user.getUserId());
            auth.setUserDetailsInternally(updateUserInternalDTO);
        }
        // Invalidate the reference token
        referenceTokenService.invalidateReferenceToken(referenceToken);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
    @PostMapping("/forget-password")
    public ResponseEntity<?> forgetPassword(@RequestBody Map<String, String> requestBody, HttpServletRequest request) {
        String username = requestBody.get("username");
        String email = requestBody.get("email");
        String phoneNumber = requestBody.get("phoneNumber");
        String resetMethod = requestBody.get("resetMethod"); // "email" or "phone"

        // Validate that at least one identifier is provided
        if(auth.ValidateIdentifier(username,phoneNumber,email))
        {
            return ResponseEntity.badRequest().body(Map.of("error", "Username, email, or phone number is required"));
        }
        // Validate reset method
        if(auth.Validate(resetMethod))
        {
            return ResponseEntity.badRequest().body(Map.of("error", "Valid reset method (email or phone) is required"));
        }
        try {
            // Determine which method to use for identifying the user
            Auth.LoginMethod loginMethod = auth.determineLoginMethod(username, email, phoneNumber);

            // Find the user
            UserDTO user=auth.findUser(loginMethod,username,phoneNumber,email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
            }

            // Generate reset token or OTP based on reset method
            if (resetMethod.equals("email")) {
                // Generate reset token
                String resetToken=auth.GenerateToken(user.getUserId(),user.getEmail());
                // Generate reset link and send email
                 String resetLink = auth.generateResetLink(request);
                 String message=emailService.sendPasswordResetEmail(user.getEmail(), resetLink,resetToken);

                return ResponseEntity.ok(Map.of(
                        "message", message,
                        "email", auth.maskEmail(user.getEmail())
                ));

            } else {
//                // Always use hardcoded OTP "123456" for testing
//                String otp = "123456";
//
//                // Store hardcoded OTP with user's phone (in case you need to verify it later)
//                // In a real system, you would generate a unique OTP for each reset request
//                Map<String, String> otpMap = new HashMap<>();
//                otpMap.put(user.getPhoneNumber(), otp);
                return auth.handelPhoneVerification(user.getPhoneNumber(), "http://karynest-real-state.azurewebsites.net/v1/auth/verify-otp-for-reset");
//                // In production, send real SMS with OTP
//                smsService.sendOtp(user.getPhoneNumber(), otp);
            }

        } catch (Exception e) {
            logger.error("Password reset error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Password reset failed"));
        }
    }
    /**
     * Verify OTP and complete password reset (for phone method)
     */
    @PostMapping("/verify-otp-for-reset") // Verifies OTP for password reset
    public ResponseEntity<?> verifyResetOtp(@RequestBody Map<String, String> requestBody) {
        String phoneNumber = requestBody.get("phoneNumber");
        String otp = requestBody.get("otp");
        String newPassword = requestBody.get("newPassword");

        if (auth.isNullOrEmpty(phoneNumber) || auth.isNullOrEmpty(otp) || auth.isNullOrEmpty(newPassword)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Phone number, OTP, and new password are required"));
        }

        try {
            // Always verify against hardcoded OTP "123456"
            boolean isOtpValid = auth.verifyOtp(phoneNumber,otp);

            if (!isOtpValid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid OTP or Phone number"));
            }

            // Get user
             UserDTO user = auth.getUserDetails("phoneNumber",phoneNumber);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            // Update password
            auth.updatePassword(user.getUserId(),newPassword);

            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));

        } catch (Exception e) {
            logger.error("OTP verification error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Password reset failed"));
        }
    }
    /**
     * Complete password reset using token (for email method)
     */
    @PostMapping("/reset-password-using-token")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> requestBody) {
        String token = requestBody.get("token");
        String newPassword = requestBody.get("newPassword");

        if (auth.isNullOrEmpty(token) || auth.isNullOrEmpty(newPassword)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Token and new password are required"));
        }

        try {
            // Verify token and get user ID
            Long userId = auth.verifyPasswordResetToken(token);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            // Update password
            auth.updatePassword(userId, newPassword);

            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));

        } catch (Exception e) {
            logger.error("Password reset error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Password reset failed"));
        }
    }


}
