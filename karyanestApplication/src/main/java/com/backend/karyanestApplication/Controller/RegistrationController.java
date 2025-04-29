
package com.backend.karyanestApplication.Controller;

import com.backend.karyanestApplication.DTO.UserRegistrationDTO;
import com.backend.karyanestApplication.DTO.parentDto;
import com.backend.karyanestApplication.Model.*;
import com.backend.karyanestApplication.Service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Controller for Authentication operations.
 */
@RestController
@RequestMapping("/v1/auth")
@Tag(name = "Authentication", description = "Authentication operations")
public class RegistrationController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(LeadsController.class);
    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }
           //response.put("verificationUrl", EMAIL_VERIFICATION_TEMPLATE + "?email=" + registeredUser.getEmail());
    /**
     * Register a new user.
     *
     * @param userDTO The user registration details
     * @return ResponseEntity with registration message and verification instructions
     */
    @Operation(summary = "Register new user", description = "Register a new user and send verification via preferred method")
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody UserRegistrationDTO userDTO,
            @RequestParam(name = "parent_id", required = false, defaultValue = "0") Long parentId) {
        try {
            User registeredUser = userService.register(userDTO, parentId);
            return userService.notifyUser(registeredUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Registration failed", "message", e.getMessage()));
        }
    }

    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<?> getParentUser(@PathVariable Long id) {
        List<User> users = userService.getParentUser(id);
        if (users != null && !users.isEmpty()) {
            // Using DTO to prevent excessive data from linked entities
            List<parentDto> userDTOs = users.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(userDTOs);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
//    /**
//     * Verify a user's email.
//     *
//     * @param email The email of the user to be verified
//     * @return ResponseEntity with verification message
//     */
//    @PostMapping("/verify")
//    public ResponseEntity<Map<String, Object>> verifyUser(@RequestParam String email, @RequestParam String token) {
//        User user = userService.getUserByEmail(email);
//
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(Map.of("error", "User not found with this email."));
//        }
//          auth.verifyPasswordResetToken(token);
//        if (user.getVerificationStatus() == User.VerificationStatus.Verified) {
//            return ResponseEntity.ok(Map.of("message", "Your account is already verified."));
//        }
//
//        auth.verifyUser(email);
//        return ResponseEntity.ok(Map.of("message", "User verified successfully using Email Service."));
//    }
//
//
//    /**
//     * Verify a user's phone number during the registration process.
//     *
//     * @param request A map containing phoneNumber and OTP.
//     * @return ResponseEntity with a verification message.
//     */
//    @Operation(summary = "Verify user phone", description = "Verify a user's phone number during registration")
//    @PostMapping("/verify-user-otp")
//    public ResponseEntity<Map<String, Object>> verifyRegistrationOtp(@RequestBody Map<String, String> request) {
//        String phoneNumber = request.get("phoneNumber");
//        String otpEntered = request.get("otp");
//
//        if (phoneNumber == null || phoneNumber.trim().isEmpty() ||
//                otpEntered == null || otpEntered.trim().isEmpty()) {
//            return ResponseEntity.badRequest().body(
//                    Map.of("error", "Phone number and OTP are required.")
//            );
//        }
//
//        try {
//            boolean isVerified = userService.verifyRegistrationOtp(phoneNumber, otpEntered);
//
//            if (!isVerified) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
//                        Map.of("error", "Invalid OTP or phone number.")
//                );
//            }
//
//            return ResponseEntity.ok(
//                    Map.of("message", "User verified successfully using OTP service.")
//            );
//
//        } catch (Exception e) {
//            logger.error("OTP verification failed", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
//                    Map.of("error", "Something went wrong during OTP verification. Please try again later.")
//            );
//        }
//    }
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
//        String username = request.get("username");
//        String phoneNumber = request.get("phoneNumber");
//        String email = request.get("email");
//        String password = request.get("password");
//
//        try {
//            // Case 1: If only phone number is provided (no password), send OTP
//            if (!isNullOrEmpty(phoneNumber) &&
//                    isNullOrEmpty(username) &&
//                    isNullOrEmpty(email) &&
//                    isNullOrEmpty(password)) {
//
//                User user = userService.findByPhoneNumber(phoneNumber);
//
//                if(user==null)
//                {
//                    throw new CustomException("User not found with phone number:" + phoneNumber);
//                }
//                return handlePhoneVerification(user, "http://karynest-real-state.azurewebsites.net/v1/auth/verify-otp");
//            }
//
//            // Case 2: Username/email/phone + password authentication
//            if (isNullOrEmpty(password)) {
//                return ResponseEntity.badRequest().body(Map.of("error", "Password is required for  authentication"));
//            }
//
//            if (isNullOrEmpty(username) && isNullOrEmpty(email) && isNullOrEmpty(phoneNumber)) {
//                return ResponseEntity.badRequest().body(Map.of("error", "Username, email, or phone number is required"));
//            }
//
//            // Determine login method and authenticate
//            LoginMethod loginMethod = determineLoginMethod(username, email, phoneNumber);
//            User user = authenticateUser(loginMethod, username, email, phoneNumber, password);
//            if (user.getStatus() == User.UserStatus.Deleted) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(Map.of("error", "Your account has been deleted. Please contact support if you wish to restore it."));
//            }
//            if (user.getVerificationStatus() == User.VerificationStatus.Unverified) {
//                return userService.notifyUser(user, UserService.Phase.LOGIN);
//            }
//            // Generate token for authenticated user
//            return jwtService.generateAuthResponseForUser(user);
//
//        } catch (CustomException e)
//        {
//            // Log failed attempt
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("error", e.getMessage()));
//        }
//        catch (AuthenticationException e) {
//            // Log failed attempt
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("error", "Invalid credentials"));
//        } catch (Exception e) {
//            logger.error("Login error", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "Authentication failed"));
//        }
//    }
//
//    private User authenticateUser(LoginMethod method, String username, String email, String phoneNumber, String password) {
//        String loginIdentifier = username;
//
//        if (method == LoginMethod.EMAIL) {
//            loginIdentifier = userService.getUsernameByemail(email);
//            if (loginIdentifier == null) {
//                throw new AuthenticationException("Invalid email") {};
//            }
//        } else if (method == LoginMethod.PHONE) {
//            loginIdentifier = userService.getUsernameByPhoneNumber(phoneNumber);
//            if (loginIdentifier == null) {
//                throw new AuthenticationException("Invalid phone number") {};
//            }
//        }
//        // Authenticate user
//        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginIdentifier, password));
//
//        // Return user object
//        return userService.getUserByUsername(loginIdentifier);
//    }
//   private ResponseEntity<?> handlePhoneVerification(User user, String verificationUrl) {
//    String userPhone = user.getPhoneNumber();
//
//    // Generate and store OTP with expiration
//    String otp = userService.generateAndStoreOtp(userPhone);
//
//    // Send OTP securely (implementation depends on your SMS service)
//    return ResponseEntity.ok(Map.of(
//            "message", "Verification code sent",
//            "phoneNumber", maskPhoneNumber(userPhone),
//            "verificationUrl", verificationUrl
//    ));
//}
//
//
//    private String maskPhoneNumber(String phoneNumber) {
//        // Show only last 4 digits
//        if (phoneNumber.length() > 4) {
//            return "****" + phoneNumber.substring(phoneNumber.length() - 4);
//        }
//        return "****";
//    }
//
//    enum LoginMethod {
//        USERNAME, EMAIL, PHONE
//    }
//    /**
//     * Verify a user with OTP and phone number for login
//     *
//     * @param request The request containing OTP and phone number for verification
//     * @return ResponseEntity with verification message
//     */
//    @PostMapping("/verify-otp")
//    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
//        String phoneNumber = request.get("phoneNumber");
//        String otpEntered = request.get("otp");
//
//        if (phoneNumber == null || phoneNumber.isEmpty() || otpEntered == null || otpEntered.isEmpty()) {
//            return ResponseEntity.badRequest().body(Map.of("error occurred", "Phone number and OTP are required"));
//        }
//
//        // Use the service to verify OTP
//        boolean isOtpValid = userService.verifyLoginOtp(phoneNumber, otpEntered);
//
//        if (!isOtpValid) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid OTP"));
//        }
//
//        // Fetch User & Role
//        User user = userService.getUserByPhoneNumber(phoneNumber);
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));
//        }
//        return jwtService.generateAuthResponseForUser(user);
//    }
//
//    /**
//     * Validate a reference token.
//     *
//     * @param referenceToken The reference token to be validated
//     * @return ResponseEntity with JWT token
//     */
//    @Operation(summary = "Validate reference token", description = "Validate a reference token and return the associated JWT token")
//    @GetMapping("/validateReferenceToken")
//    public ResponseEntity<Map<String, String>> validateReferenceToken(@RequestParam String referenceToken) {
//        String jwt = referenceTokenService.getJwtFromReferenceToken(referenceToken);
//        if (jwt == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or expired reference token. Please log in again."));
//        }
//        return ResponseEntity.ok(Map.of("jwtToken", jwt));
//    }
//
//    /**
//     * Logout a user.
//     *
//     * @param request Map containing the reference token
//     * @return ResponseEntity with logout message
//     */
//    @Operation(summary = "User logout", description = "Invalidate a reference token and logout the user")
//    @PostMapping("/logout")
//    public ResponseEntity<Map<String, String>> logout(@RequestBody Map<String, String> request) {
//        String referenceToken = request.get("referenceToken");
//        if (referenceToken == null || referenceToken.isEmpty()) {
//            return ResponseEntity.badRequest().body(Map.of("error", "Reference token is required"));
//        }
//
//        // Get the username from the reference token
//        String jwt = referenceTokenService.getJwtFromReferenceToken(referenceToken);
//        String username = jwtUtil.extractUsername(jwt);
//        if (username == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid reference token"));
//        }
//        // Get the user and update status to inactive
//        User user = userService.getUserByUsername(username);
//        if (user != null) {
//            user.setStatus(User.UserStatus.Inactive);
//            userService.updateUserStatus(user);
//        }
//        // Invalidate the reference token
//        referenceTokenService.invalidateReferenceToken(referenceToken);
//        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
//    }
//    @PostMapping("/forget-password")
//    public ResponseEntity<?> forgetPassword(@RequestBody Map<String, String> requestBody, HttpServletRequest request) {
//        String username = requestBody.get("username");
//        String email = requestBody.get("email");
//        String phoneNumber = requestBody.get("phoneNumber");
//        String resetMethod = requestBody.get("resetMethod"); // "email" or "phone"
//
//        // Validate that at least one identifier is provided
//        if (isNullOrEmpty(username) && isNullOrEmpty(email) && isNullOrEmpty(phoneNumber)) {
//            return ResponseEntity.badRequest().body(Map.of("error", "Username, email, or phone number is required"));
//        }
//
//        // Validate reset method
//        if (isNullOrEmpty(resetMethod) || (!resetMethod.equals("email") && !resetMethod.equals("phone"))) {
//            return ResponseEntity.badRequest().body(Map.of("error", "Valid reset method (email or phone) is required"));
//        }
//
//        try {
//            // Determine which method to use for identifying the user
//            LoginMethod loginMethod = determineLoginMethod(username, email, phoneNumber);
//
//            // Find the user
//            User user;
//            switch (loginMethod) {
//                case USERNAME:
//                    user = userService.getUserByUsername(username);
//                    break;
//                case EMAIL:
//                    user = userService.getUserByEmail(email);
//                    break;
//                case PHONE:
//                    user = userService.getUserByPhoneNumber(phoneNumber);
//                    break;
//                default:
//                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid identification method"));
//            }
//
//            if (user == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
//            }
//
//            // Generate reset token or OTP based on reset method
//            if (resetMethod.equals("email")) {
//                // Generate reset token
//                String resetToken=userService.GenerateToken(user);
//                // Generate reset link and send email
//                 String resetLink = generateResetLink(request);
//                 String message=emailService.sendPasswordResetEmail(user.getEmail(), resetLink,resetToken);
//
//                return ResponseEntity.ok(Map.of(
//                        "message", message,
//                        "email", maskEmail(user.getEmail())
//                ));
//
//            } else {
////                // Always use hardcoded OTP "123456" for testing
////                String otp = "123456";
////
////                // Store hardcoded OTP with user's phone (in case you need to verify it later)
////                // In a real system, you would generate a unique OTP for each reset request
////                Map<String, String> otpMap = new HashMap<>();
////                otpMap.put(user.getPhoneNumber(), otp);
//                return handlePhoneVerification(user, "http://karynest-real-state.azurewebsites.net/v1/auth/verify-otp-for-reset");
////                // In production, send real SMS with OTP
////                smsService.sendOtp(user.getPhoneNumber(), otp);
//            }
//
//        } catch (Exception e) {
//            logger.error("Password reset error", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "Password reset failed"));
//        }
//    }
//    /**
//     * Verify OTP and complete password reset (for phone method)
//     */
//    @PostMapping("/verify-otp-for-reset") // Verifies OTP for password reset
//    public ResponseEntity<?> verifyResetOtp(@RequestBody Map<String, String> requestBody) {
//        String phoneNumber = requestBody.get("phoneNumber");
//        String otp = requestBody.get("otp");
//        String newPassword = requestBody.get("newPassword");
//
//        if (isNullOrEmpty(phoneNumber) || isNullOrEmpty(otp) || isNullOrEmpty(newPassword)) {
//            return ResponseEntity.badRequest()
//                    .body(Map.of("error", "Phone number, OTP, and new password are required"));
//        }
//
//        try {
//            // Always verify against hardcoded OTP "123456"
//            boolean isOtpValid = userService.verifyOtp(phoneNumber,otp);
//
//            if (!isOtpValid) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body(Map.of("error", "Invalid OTP or Phone number"));
//            }
//
//            // Get user
//            User user = userService.getUserByPhoneNumber(phoneNumber);
//            if (user == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(Map.of("error", "User not found"));
//            }
//
//            // Update password
//            userService.updatePassword(user.getId(),newPassword);
//
//            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
//
//        } catch (Exception e) {
//            logger.error("OTP verification error", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "Password reset failed"));
//        }
//    }
//    /**
//     * Complete password reset using token (for email method)
//     */
//    @PostMapping("/reset-password-using-token")
//    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> requestBody) {
//        String token = requestBody.get("token");
//        String newPassword = requestBody.get("newPassword");
//
//        if (isNullOrEmpty(token) || isNullOrEmpty(newPassword)) {
//            return ResponseEntity.badRequest().body(Map.of("error", "Token and new password are required"));
//        }
//
//        try {
//            // Verify token and get user ID
//            Long userId = userService.verifyPasswordResetToken(token);
//            if (userId == null) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body(Map.of("error", "Invalid or expired token"));
//            }
//
//            // Update password
//            userService.updatePassword(userId, newPassword);
//
//            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
//
//        } catch (Exception e) {
//            logger.error("Password reset error", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "Password reset failed"));
//        }
//    }
//    // Helper methods
//    private boolean isNullOrEmpty(String str) {
//        return str == null || str.isEmpty();
//    }
//
//    private LoginMethod determineLoginMethod(String username, String email, String phoneNumber) {
//        if (!isNullOrEmpty(username)) {
//            return LoginMethod.USERNAME;
//        } else if (!isNullOrEmpty(email)) {
//            return LoginMethod.EMAIL;
//        } else {
//            return LoginMethod.PHONE;
//        }
//    }
//    private String generateResetLink(HttpServletRequest request) {
//        String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
//        return baseUrl + "/reset-password-using-token";
//    }
//    // Helper method to mask email
//    private String maskEmail(String email) {
//        if (email == null || !email.contains("@")) {
//            return "****";
//        }
//
//        String[] parts = email.split("@");
//        String name = parts[0];
//        String domain = parts[1];
//
//        String maskedName;
//        if (name.length() <= 2) {
//            maskedName = "*".repeat(name.length());
//        } else {
//            maskedName = name.substring(0, 2) + "*".repeat(name.length() - 2);
//        }
//
//        return maskedName + "@" + domain;
//    }
    private parentDto convertToDTO(User user) {
        parentDto dto = new parentDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullname(user.getFullName());
        // Add other fields as needed, but exclude collections like properties, notifications, etc.
        return dto;
    }
}
