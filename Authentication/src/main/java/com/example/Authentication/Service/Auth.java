package com.example.Authentication.Service;
import com.example.Authentication.DTO.AuthResponseDTO;
import com.example.Authentication.DTO.UpdateUserInternalDTO;
import com.example.Authentication.DTO.UserDTO;
import com.example.Authentication.Interface.AuthHelper;
import com.example.Authentication.Interface.AuthService;
import com.example.Authentication.Model.Otpdata;
import com.example.Authentication.Model.PasswordResetToken;
import com.example.Authentication.Model.UserInternalUpdateEntity;
//import com.example.Authentication.Repositery.OtpRepository;
import com.example.Authentication.Repositery.OtpRepository;
import com.example.Authentication.Repositery.UserInternalUpdateRepository;
import com.example.Authentication.UTIL.JwtUtil;
import com.example.module_b.ExceptionAndExceptionHandler.CustomException;
import com.example.Authentication.Repositery.PasswordResetTokenRepository;
import com.example.rbac.Model.RolesPermission;
import com.example.rbac.Repository.RolesPermissionRepository;
import com.example.rbac.Repository.RolesRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class Auth implements AuthService, AuthHelper {
    // Configuration constants
    private static final String VERIFICATION_LINK_TEMPLATE = "https://nestaro.in/v1/auth/verify";
    private final SmsService sendOtpViaSms;
    private final UserHandleService userHandleService;

    // Dependencies
    private static final Logger logger = LoggerFactory.getLogger(Auth.class);
    private final AuthenticationManager authenticationManager;
    private final RestTemplate restTemplate;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final RolesRepository roleRepository;
    private final JwtUtil jwtUtil;
    private final ReferenceTokenService referenceTokenService;
    private final RolesPermissionRepository rolesPermissionRepository;
    private final RedisService redisService;
    @Autowired
    private OtpService otpService;
    @Autowired
    private UserInternalUpdateRepository userInternalUpdateRepository;
    public enum LoginMethod {
        EMAIL, PHONE, USERNAME
    }

    // Constructor with all required dependencies
    @Autowired
    public Auth(
            SmsService sendOtpViaSms, UserHandleService userHandleService, AuthenticationManager authenticationManager,
            RestTemplate restTemplate,
            EmailService emailService,
            PasswordResetTokenRepository passwordResetTokenRepository,
            RolesRepository roleRepository,
            JwtUtil jwtUtil,
            ReferenceTokenService referenceTokenService,
            RolesPermissionRepository rolesPermissionRepository, RedisService redisService) {
            this.sendOtpViaSms = sendOtpViaSms;
        this.userHandleService = userHandleService;

        this.authenticationManager = authenticationManager;
        this.restTemplate = restTemplate;
        this.emailService = emailService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.roleRepository = roleRepository;
        this.jwtUtil = jwtUtil;
        this.referenceTokenService = referenceTokenService;
        this.rolesPermissionRepository = rolesPermissionRepository;
        this.redisService = redisService;
    }

    @Override
    public LoginMethod determineLoginMethod(String username, String email, String PhoneNumber) {
        if (!isNullOrEmpty(username)) {
            return LoginMethod.USERNAME;
        } else if (!isNullOrEmpty(email)) {
            return LoginMethod.EMAIL;
        } else {
            return LoginMethod.PHONE;
        }
    }

    @Override
    public String generateResetLink(HttpServletRequest request) {
        String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        System.out.println(baseUrl);
        return baseUrl + "/reset-password-using-token";
    }

    @Override
    public String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "****";
        }

        String[] parts = email.split("@");
        String name = parts[0];
        String domain = parts[1];

        String maskedName;
        if (name.length() <= 2) {
            maskedName = "*".repeat(name.length());
        } else {
            maskedName = name.substring(0, 2) + "*".repeat(name.length() - 2);
        }

        return maskedName + "@" + domain;
    }

    public UserDTO authenticateUser(LoginMethod method, String username, String email, String phoneNumber, String password) {
        String loginIdentifier = username;
        if (method == LoginMethod.EMAIL) {
            loginIdentifier = loginWithEmail(email);
        } else if (method == LoginMethod.PHONE) {
            loginIdentifier = loginWithPhone(phoneNumber);
        }
        // Authenticate user
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginIdentifier, password));
        // Return user object
        return userHandleService.getUserDetails("username", loginIdentifier);
    }

    @Override
    public boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    @Override
    public ResponseEntity<?> handelPhoneVerification(String phoneNumber, String verificationUrl) {
        logger.info("Starting phone verification for: {}", phoneNumber);

        try {
            // Validate phone number first
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Phone number is required",
                        "error", "INVALID_PHONE_NUMBER"
                ));
            }

            // Generate and store OTP
            String otp = otpService.generateAndStoreOtp(phoneNumber);

            if (otp == null || otp.isEmpty()) {
                logger.error("Failed to generate OTP for phone: {}", phoneNumber);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                        "success", false,
                        "message", "Failed to generate verification code",
                        "error", "OTP_GENERATION_FAILED"
                ));
            }

            // Send OTP via SMS
            try {
                sendOtpViaSms.sendOtp(phoneNumber, otp);
                logger.info("OTP sent successfully to phone: {}", phoneNumber);

                // Success Response
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Verification code sent successfully",
                        "phoneNumber", maskPhoneNumber(phoneNumber),
                        "verificationUrl", verificationUrl,
                        "timestamp", System.currentTimeMillis(),
                        // Remove this line in production
                        "otp", otp // Only for testing phase
                ));

            } catch (CustomException e) {
                logger.error("CustomException while sending OTP to phone: {}. Error: {}", phoneNumber, e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                        "success", false,
                        "message", "Failed to send verification code",
                        "error", e.getMessage(),
                        "errorCode", "SMS_SEND_FAILED"
                ));

            } catch (Exception e) {
                logger.error("IOException while sending OTP to phone: {}. Error: {}", phoneNumber, e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                        "success", false,
                        "message", "Network error while sending verification code",
                        "error", "NETWORK_ERROR",
                        "details", e.getMessage()
                ));
            }

        } catch (CustomException e) {
            logger.error("CustomException during OTP generation for phone: {}. Error: {}", phoneNumber, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Failed to process verification request",
                    "error", e.getMessage(),
                    "errorCode", "OTP_PROCESS_FAILED"
            ));

        } catch (Exception e) {
            logger.error("Unexpected error during phone verification for: {}. Error: {}", phoneNumber, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Internal server error",
                    "error", "INTERNAL_SERVER_ERROR",
                    "details", e.getMessage()
            ));
        }
    }


    @Override
    public String maskPhoneNumber(String phoneNumber) {
        // Show only last 4 digits
        if (phoneNumber.length() > 4) {
            return "****" + phoneNumber.substring(phoneNumber.length() - 4);
        }
        return "****";
    }

    @Override
    public String loginWithEmail(String email) {
        UserDTO user = userHandleService.getUserDetails("email", email);
        String loginIdentifier = user.getUsername();
        if (loginIdentifier == null) {
            throw new AuthenticationException("Invalid email") {
            };
        }
        return loginIdentifier;
    }

    @Override
    public String loginWithPhone(String PhoneNumber) {
        UserDTO user = userHandleService.getUserDetails("phoneNumber", PhoneNumber);
        String loginIdentifier = user.getUsername();
        if (loginIdentifier == null) {
            throw new AuthenticationException("Invalid phone number") {
            };
        }
        return loginIdentifier;
    }

    @Override
    public ResponseEntity<?> loginWithPhoneAndOtp(String Phone) {
        UserDTO user = userHandleService.getUserDetails("phoneNumber", Phone);
        if (user == null) {
            throw new CustomException("User not found with phone number:" + Phone);
        }
        return handelPhoneVerification(Phone, "https://nestaro.in/v1/auth/verify-otp");
    }

    @Override
    public boolean Check(String phoneNumber, String username, String email, String password) {
        return !isNullOrEmpty(phoneNumber) &&
                isNullOrEmpty(username) &&
                isNullOrEmpty(email) &&
                isNullOrEmpty(password);
    }

    @Override
    public UserDTO findUser(LoginMethod loginMethod, String username, String phoneNumber, String email) {
        if (loginMethod == null) {
            throw new CustomException("Invalid identification method");
        }
        return switch (loginMethod) {
            case USERNAME -> userHandleService.getUserDetails("username", username);
            case EMAIL -> userHandleService.getUserDetails("email", email);
            case PHONE -> userHandleService.getUserDetails("phoneNumber", phoneNumber);
        };
    }

    public boolean ValidateIdentifier(String username, String phoneNumber, String email) {
        return !isNullOrEmpty(username) || !isNullOrEmpty(email) || !isNullOrEmpty(phoneNumber);
    }

    public boolean validateResetMethod(String resetMethod) {
        if (isNullOrEmpty(resetMethod)) {
            return false; // fail if null or empty
        }
        return resetMethod.equals("email") || resetMethod.equals("phone");
    }

    public ResponseEntity<?> handleLoginRequest(String username, String phoneNumber, String email, String password) {
        // OTP Login - phone number only
        if (Check(phoneNumber, username, email, password)) {
            return loginWithPhoneAndOtp(phoneNumber);
        }

        // Password Login - needs at least one identifier + password
        if (isNullOrEmpty(password)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password is required for authentication"));
        }
        if (isNullOrEmpty(username) && isNullOrEmpty(email) && isNullOrEmpty(phoneNumber)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username, email, or phone number is required"));
        }

        // Handle blocked users
        String input;
        if (!isNullOrEmpty(username)) {
            input = username;
        } else if (!isNullOrEmpty(email)) {
            input = email;
        } else {
            input = phoneNumber;
        }

        String key;
        if (input.contains("@")) {
            key = "email";
        } else if (input.matches("\\d+")) {
            key = "phonenumber";
        } else {
            key = "username";
        }
        UserDTO users = userHandleService.getUserDetails(key, input);
        Long userId = users.getUserId();
        boolean isInRedis = redisService.isUserBlocked(userId);
        if (Objects.equals(users.getStatus(), "Blocked")) {
            if (!isInRedis) {
                redisService.addToBlockedUsers(userId); // Sync Redis with DB
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Your account is blocked. Please contact admin."));
        }
        if (isInRedis) {
            redisService.removeFromBlockedUsers(userId);
        }

        LoginMethod loginMethod = determineLoginMethod(username, email, phoneNumber);
        UserDTO user = authenticateUser(loginMethod, username, email, phoneNumber, password);

        if (Objects.equals(user.getStatus(), "Deleted")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Your account has been deleted. Please contact support if you wish to restore it."));
        }
        if (Objects.equals(user.getVerificationStatus(), "Unverified")) {
            return notifyUser(user);
        }

        return generateAuthResponseForUser(user);
    }

    /**
     * Save a password reset token for a user
     *
     * @param userId The ID of the user
     * @param resetToken The token to save
     */
    @Transactional
    public void saveToken(Long userId, String resetToken) {
        // Create expiration time (e.g., 1 hour from now)
        Instant expiryDate = Instant.now().plus(1, ChronoUnit.HOURS);

        // Create and save the token entity
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(resetToken);
        token.setUserId(userId);
        token.setExpiryDate(expiryDate);

        passwordResetTokenRepository.save(token);
    }

    public String GenerateToken(Long userId, String email) {
        // Generate reset token
        String Token = UUID.randomUUID().toString();
        // Store token in database with expiration
        saveToken(userId, Token);
        // Send verification email
        sendVerificationEmail(email, Token);
        return Token;
    }

    private ResponseEntity<?> sendLoginResponse(UserDTO user) {
        String verificationType;
        String verificationUrl;

        if (Objects.equals(user.getVerificationMethod(), "Phone")) {
            // Phone verification scenario
            String otp = otpService.generateAndStoreOtp(user.getPhoneNumber());
            // Here you would integrate with SMS service to send the OTP
            verificationType = "SMS";
            verificationUrl = "https://nestaro.in/v1/auth/verify-user-otp";
        } else {
            // Email verification scenario
            String token = GenerateToken(user.getUserId(), user.getEmail());
            verificationType = "email";
            verificationUrl = "https://nestaro.in/v1/auth/verify?email=" +
                    user.getEmail() + "&token=" + token;
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "verificationType", verificationType,
                        "verificationUrl", verificationUrl,
                        "verificationStatus", "Your account is not verified.",
                        "message", "Please complete the verification process.",
                        "username", user.getUsername(),
                        "verificationMethod", user.getVerificationMethod(),
                        "actionRequired", "Please verify your " + verificationType + " to unlock full platform features."
                ));
    }

    public ResponseEntity<?> notifyUser(UserDTO user) {
        return sendLoginResponse(user);
    }

//    @Transactional
//    public void updatePassword(Long userId, String newPassword) {
//        if (!isValidPassword(newPassword)) {
//            System.out.println();
//            throw new CustomException("Password does not meet security requirements");
//        }
//        UserDTO user = getUserDetails("UserId", Long.toString(userId));
//        //settingPsswordmethod
//        UpdateUserInternalDTO updateUserInternalDTO = new UpdateUserInternalDTO();
//        updateUserInternalDTO.setNewPassword(newPassword);
//        updateUserInternalDTO.setUserId(user.getUserId());
//        setUserDetailsInternally(updateUserInternalDTO);
//    }
    public Long verifyToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);

        if (resetToken == null) {
            throw new CustomException("Invalid verification token.");
        }

        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            passwordResetTokenRepository.delete(resetToken); // Optional: clean up expired tokens
            throw new CustomException("Token has expired. Please request a new one.");
        }

        return resetToken.getUserId();
    }

    /**
     * Send verification email to user.
     *
     * @param email the user's email address
     * @param token the verification token
     * @throws CustomException if email sending fails
     */
    public void sendVerificationEmail(String email, String token) {
        try {
            String verificationLink = VERIFICATION_LINK_TEMPLATE + "?email=" + email;
            String emailBody = "Click the link to verify your account: <a href=\"" + verificationLink + "\">Verify Now</a>"
                    + "<br><br><strong>Note:</strong> Your verification token is <b>" + token + "</b>. "
                    + "Without this token, verification will not be completed.";

            emailService.sendVerificationEmail(email, "Verify your email", emailBody);
        } catch (MessagingException e) {
            throw new CustomException("Failed to send verification email: " + e.getMessage());
        }
    }
    /**
     * Update user's last login time.
     *
     * @param user the user to update
     */
    @Transactional
    public void updateUserLastLogin(UserDTO user) {
        UpdateUserInternalDTO updateUserInternalDTO = new UpdateUserInternalDTO();
        updateUserInternalDTO.setLastLogin(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")));
        updateUserInternalDTO.setUserId(user.getUserId());
        userHandleService.setUserDetailsInternally(updateUserInternalDTO);
    }

    /**
     * Verify a user's email.
     *
     * @param user the user to verify
     * @throws CustomException if user is null
     */
    @Transactional
    public void verifyUser(UserDTO user) {
        try {
            if (user == null || user.getUserId() == null) {
                throw new CustomException("User not found");
            }

            user.setVerificationStatus("Verified");

            UpdateUserInternalDTO updateUserInternalDTO = new UpdateUserInternalDTO();
            updateUserInternalDTO.setUserId(user.getUserId());

            userHandleService.setUserDetailsInternally(updateUserInternalDTO);
        } catch (Exception e) {
            throw new CustomException("Error verifying user: " + e.getMessage());
        }
    }
    /**
     * Generate authentication response for a user
     *
     * @param user the user to generate authentication for
     * @return ResponseEntity with auth response or error
     */
    public ResponseEntity<?> generateAuthResponseForUser(UserDTO user) {
        // Check if user is verified
        if (Objects.equals(user.getVerificationStatus(), "Unverified")) {
            return notifyUser(user);
        }
        // Generate JWT token
        Long userRoleId = user.getRoleId();
        String UserRole = user.getRole();
        // Fetch permissions for the user
        List<RolesPermission> rolePermissions = rolesPermissionRepository.findPermissionsByRoleId(userRoleId);
        List<String> permissionList = rolePermissions.stream()
                .map(rp -> rp.getPermissions().getPermission())
                .collect(Collectors.toList());

        // Generate JWT token with permissions
        String jwtToken = jwtUtil.generateToken(user.getUsername(), UserRole, user.getUserId(), user.getFullName(), permissionList);
        String refreshToken = referenceTokenService.generateReferenceToken(jwtToken);
        UpdateUserInternalDTO updateUserInternalDTO = new UpdateUserInternalDTO();
        System.out.println(user.getUserId());
        updateUserInternalDTO.setUserId(user.getUserId());
        updateUserInternalDTO.setStatus("Active");
        userHandleService.setUserDetailsInternally(updateUserInternalDTO);
        // Generate and return response
        AuthResponseDTO authResponse = getJwtResponse(jwtToken, refreshToken, UserRole);
        return ResponseEntity.ok(authResponse);
    }

    /**
     * Create JWT response with user details and permissions.
     *
     * @param jwtToken     the JWT token
     * @param refreshToken the refresh token
     * @param userRole     the user role
     * @return the authentication response DTO
     */
    public AuthResponseDTO getJwtResponse(String jwtToken, String refreshToken, String userRole) {
        // Return JWT response with permissions
        return new AuthResponseDTO(jwtToken, refreshToken, userRole);
    }


}
