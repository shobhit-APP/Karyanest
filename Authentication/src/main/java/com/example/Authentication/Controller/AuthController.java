
package com.example.Authentication.Controller;

import com.example.Authentication.DTO.UpdateUserInternalDTO;
import com.example.Authentication.DTO.UserDTO;
import com.example.Authentication.Repositery.OtpRepository;
import com.example.Authentication.Service.*;
import com.example.module_b.ExceptionAndExceptionHandler.CustomException;

import com.example.Authentication.Component.UserContext;
//import com.example.Authentication.Service.JwtService;
import com.example.Authentication.UTIL.JwtUtil;
import com.example.Authentication.Service.Auth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private EmailService emailService;
    @Autowired
    private Auth auth;
    private final OtpService otpService;
    @Autowired
    private UserHandleService userHandleService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    public AuthController(JwtUtil jwtUtil, ReferenceTokenService referenceTokenService,
                          AuthenticationManager authenticationManager, OtpService otpService) {
        this.jwtUtil = jwtUtil;
        this.referenceTokenService = referenceTokenService;
        this.otpService = otpService;
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
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Authentication failed"));
        }
    }
    /**
     * Endpoint to verify a user's email using a token.
     *
     * @param email the email of the user to verify
     * @param token the verification token
     * @return ResponseEntity with success or error message
     */
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyUser(
            @RequestParam String email,
            @RequestParam String token) {

        try {
            // Step 1: Get user by email
            UserDTO user = userHandleService.getUserDetails("email", email);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found with this email."));
            }

            // Step 2: Check if already verified
            if ("Verified".equalsIgnoreCase(user.getVerificationStatus())) {
                return ResponseEntity.ok(Map.of("message", "Your account is already verified."));
            }

            // Step 3: Verify token and match with user
            Long tokenUserId = auth.verifyToken(token);
            if (!user.getUserId().equals(tokenUserId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token does not match the user."));
            }

            // Step 4: Proceed with verification
            auth.verifyUser(user);
            return ResponseEntity.ok(Map.of("message", "User verified successfully via Email Service."));

        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Verification error for email: {}", email, e);
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
            boolean isVerified = otpService.verifyRegistrationOtp(phoneNumber, otpEntered);

            if (!isVerified) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        Map.of("error", "Invalid OTP or phone number.")
                );
            }
             otpService.deleteOtpByPhoneNumber(phoneNumber);
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
            boolean isOtpValid = otpService.verifyLoginOtp(phoneNumber.trim(), otpEntered.trim());

            if (!isOtpValid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        Map.of("error", "Invalid OTP Or Expired.")
                );
            }

            UserDTO user = userHandleService.getUserDetails("phoneNumber", phoneNumber.trim());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of("error", "User not found for this phone number.")
                );
            }
            otpService.deleteOtpByPhoneNumber(phoneNumber);
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
        UserDTO user = userHandleService.getUserDetails("username", username);
        if (user != null) {
            UpdateUserInternalDTO updateUserInternalDTO = new UpdateUserInternalDTO();
            updateUserInternalDTO.setStatus("Inactive");
            updateUserInternalDTO.setUserId(user.getUserId());
            userHandleService.setUserDetailsInternally(updateUserInternalDTO);
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

        // ✅ Validate that at least one identifier is provided
        if (!auth.ValidateIdentifier(username, phoneNumber, email)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username, email, or phone number is required"));
        }

        // ✅ Validate reset method
        if (!auth.validateResetMethod(resetMethod)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Valid reset method (email or phone) is required"));
        }

        try {
            // ✅ Determine the login method (username, email, or phone)
            Auth.LoginMethod loginMethod = auth.determineLoginMethod(username, email, phoneNumber);

            // ✅ Find the user by the chosen method
            UserDTO user = auth.findUser(loginMethod, username, phoneNumber, email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
            }

            // ✅ Handle password reset via email
            if (resetMethod.equals("email")) {
                String resetToken = auth.GenerateToken(user.getUserId(), user.getEmail());
                String resetLink = auth.generateResetLink(request);
                String message = emailService.sendPasswordResetEmail(user.getEmail(), resetLink, resetToken);

                return ResponseEntity.ok(Map.of(
                        "message", message,
                        "email", auth.maskEmail(user.getEmail())
                ));

            } else {
                // ✅ Handle password reset via phone (e.g., OTP flow)
                // Replace below with actual logic if OTP generation and verification is implemented
                return auth.handelPhoneVerification(user.getPhoneNumber(), "https://nestaro.in/v1/auth/verify-otp-for-reset");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Something went wrong: " + e.getMessage()));
        }
    }

    /**
     * Verify OTP and complete password reset (for phone method)
     */
//    @PostMapping("/verify-otp-for-reset") // Verifies OTP for password reset
//    public ResponseEntity<?> verifyResetOtp(@RequestBody Map<String, String> requestBody) {
//        String phoneNumber = requestBody.get("phoneNumber");
//        String otp = requestBody.get("otp");
//        String newPassword = requestBody.get("newPassword");
//
//        if (auth.isNullOrEmpty(phoneNumber) || auth.isNullOrEmpty(otp) || auth.isNullOrEmpty(newPassword)) {
//            return ResponseEntity.badRequest()
//                    .body(Map.of("error", "Phone number, OTP, and new password are required"));
//        }
//
//        try {
//            // Always verify against hardcoded OTP "123456"
//            boolean isOtpValid = auth.verifyOtp(phoneNumber, otp);
//
//            if (!isOtpValid) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body(Map.of("error", "Invalid OTP or Phone number or Otp is Expired"));
//            }
//
//            // Get user
//            UserDTO user = auth.getUserDetails("phoneNumber", phoneNumber);
//            if (user == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(Map.of("error", "User not found"));
//            }
//
//            // Update password
//            auth.updatePassword(user.getUserId(), newPassword);
//
//            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
//
//        } catch (Exception e) {
//            logger.error("OTP verification error", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "Password reset failed"));
//        }
//    }
    @PostMapping("/verify-otp-for-reset")
    public ResponseEntity<?> verifyResetOtp(@RequestBody Map<String, String> requestBody) {
        String phoneNumber = requestBody.get("phoneNumber");
        String otp = requestBody.get("otp");
        String newPassword = requestBody.get("newPassword");

        if (auth.isNullOrEmpty(phoneNumber) || auth.isNullOrEmpty(otp) || auth.isNullOrEmpty(newPassword)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Phone number, OTP, and new password are required"));
        }

        try {
            boolean isOtpValid = otpService.verifyOtp(phoneNumber, otp);

            if (!isOtpValid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid OTP or OTP expired"));
            }

            // Get user
            UserDTO user = userHandleService.getUserDetails("phoneNumber", phoneNumber);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            // Update password
            userHandleService.updatePassword(user.getUserId(), newPassword);

            // Now safely delete OTP after successful password update
            otpService.deleteOtpByPhoneNumber(phoneNumber);

            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));

        } catch (CustomException e) {
        logger.error("Password validation failed: {}", e.getMessage());
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
        logger.error("OTP verification error", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Password reset failed"));
    }

}

}
