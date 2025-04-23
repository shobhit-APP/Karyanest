package com.example.Authentication.Service;
import com.example.Authentication.DTO.AuthResponseDTO;
import com.example.Authentication.DTO.UpdateUserInternalDTO;
import com.example.Authentication.DTO.UserDTO;
import com.example.Authentication.Model.PasswordResetToken;
import com.example.Authentication.UTIL.JwtUtil;
import com.example.module_b.ExceptionAndExceptionHandler.CustomException;
import com.example.Authentication.Repositery.PasswordResetTokenRepository;
import com.example.rbac.Model.RolesPermission;
import com.example.rbac.Repository.PermissionsRepository;
import com.example.rbac.Repository.RolesPermissionRepository;
import com.example.rbac.Repository.RolesRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class Auth implements AuthService, AuthHelper {
    // Configuration constants
    private static final String VERIFICATION_LINK_TEMPLATE = "http://karynest-real-state.azurewebsites.net/v1/auth/verify?email=";

    // Dependencies
    private final AuthenticationManager authenticationManager;
    private final RestTemplate restTemplate;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final RolesRepository roleRepository;
    private final JwtUtil jwtUtil;
    private final ReferenceTokenService referenceTokenService;
    private final RolesPermissionRepository rolesPermissionRepository;

    // In-memory OTP storage
    private final Map<String, String> otpStorage = new HashMap<>();
    // In-memory registration OTP storage (separate from login OTP)
    private final Map<String, String> registrationOtpStorage = new HashMap<>();

    public enum LoginMethod {
        EMAIL, PHONE, USERNAME
    }

    // Constructor with all required dependencies
    @Autowired
    public Auth(
            AuthenticationManager authenticationManager,
            RestTemplate restTemplate,
            EmailService emailService,
            PasswordResetTokenRepository passwordResetTokenRepository,
            RolesRepository roleRepository,
            JwtUtil jwtUtil,
            ReferenceTokenService referenceTokenService,
            RolesPermissionRepository rolesPermissionRepository) {

        this.authenticationManager = authenticationManager;
        this.restTemplate = restTemplate;
        this.emailService = emailService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.roleRepository = roleRepository;
        this.jwtUtil = jwtUtil;
        this.referenceTokenService = referenceTokenService;
        this.rolesPermissionRepository = rolesPermissionRepository;
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
        return getUserDetails("username", loginIdentifier);
    }

    @Override
    public boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    @Override
    public ResponseEntity<?> handelPhoneVerification(String PhoneNumber, String verificationUrl) {
        // Generate and store OTP with expiration
        String otp = generateAndStoreOtp(PhoneNumber);

        // Send OTP securely (implementation depends on your SMS service)
        return ResponseEntity.ok(Map.of(
                "message", "Verification code sent",
                "phoneNumber", maskPhoneNumber(PhoneNumber),
                "verificationUrl", verificationUrl
        ));
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
        UserDTO user = getUserDetails("email", email);
        String loginIdentifier = user.getUsername();
        if (loginIdentifier == null) {
            throw new AuthenticationException("Invalid email") {
            };
        }
        return loginIdentifier;
    }

    @Override
    public String loginWithPhone(String PhoneNumber) {
        UserDTO user = getUserDetails("phoneNumber", PhoneNumber);
        String loginIdentifier = user.getUsername();
        if (loginIdentifier == null) {
            throw new AuthenticationException("Invalid phone number") {
            };
        }
        return loginIdentifier;
    }

    @Override
    public ResponseEntity<?> loginWithPhoneAndOtp(String Phone) {
        UserDTO user = getUserDetails("phoneNumber", Phone);
        if (user == null) {
            throw new CustomException("User not found with phone number:" + Phone);
        }
        return handelPhoneVerification(Phone, "http://karynest-real-state.azurewebsites.net/v1/auth/verify-otp");
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
            case USERNAME -> getUserDetails("username", username);
            case EMAIL -> getUserDetails("email", email);
            case PHONE -> getUserDetails("phoneNumber", phoneNumber);
        };
    }

    public boolean ValidateIdentifier(String username, String phoneNumber, String email) {
        return !isNullOrEmpty(username) || !isNullOrEmpty(email) || !isNullOrEmpty(phoneNumber);
    }

    public boolean Validate(String resetMethod) {
        return isNullOrEmpty(resetMethod) || !resetMethod.equals("email") && !resetMethod.equals("phone");
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
            String otp = generateAndStoreOtp(user.getPhoneNumber());
            // Here you would integrate with SMS service to send the OTP
            verificationType = "SMS";
            verificationUrl = "http://karynest-real-state.azurewebsites.net/v1/verify-user-otp";
        } else {
            // Email verification scenario
            String token = GenerateToken(user.getUserId(), user.getEmail());
            verificationType = "email";
            verificationUrl = "http://karynest-real-state.azurewebsites.net/v1/verify?email=" +
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

    @Transactional
    public void updatePassword(Long userId, String newPassword) {
        if (!isValidPassword(newPassword)) {
            System.out.println();
            throw new CustomException("Password does not meet security requirements");
        }
        UserDTO user = getUserDetails("UserId", Long.toString(userId));
        //settingPsswordmethod
        UpdateUserInternalDTO updateUserInternalDTO = new UpdateUserInternalDTO();
        updateUserInternalDTO.setNewPassword(newPassword);
        updateUserInternalDTO.setUserId(user.getUserId());
        setUserDetailsInternally(updateUserInternalDTO);
    }

    private boolean isValidPassword(String password) {
        // Example Password Validation Function
        return password.length() >= 8 && password.matches(".*[A-Z].*") && password.matches(".*\\d.*");
    }

    public Long verifyPasswordResetToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);

        if (resetToken == null) {
            return null; // Token does not exist
        }
        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            passwordResetTokenRepository.delete(resetToken);
            throw new IllegalStateException("Token has expired. Please request a new one.");
        }
        return resetToken.getUserId();
    }

    /**
     * Generate and store OTP for login
     *
     * @param phoneNumber the user's phone number
     * @return the generated OTP
     */
    public String generateAndStoreOtp(String phoneNumber) {
        // For simplicity, using "123" as in original code
        // In production, use a secure random generator
        String otp = "123456";
        otpStorage.put(phoneNumber, otp);
        registrationOtpStorage.put(phoneNumber,otp);
        return otp;
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
     * Generic method to verify OTP for both login and registration.
     *
     * @param phoneNumber the phone number to verify OTP against
     * @param otpEntered the OTP entered by the user
     * @param otpStorage the storage map where OTPs are stored
     * @return boolean indicating if OTP is valid
     */
    public boolean verifyOtp(String phoneNumber, String otpEntered, Map<String, String> otpStorage) {
        // Check if phone number exists in the provided OTP storage
        if (!otpStorage.containsKey(phoneNumber)) {
            return false;
        }

        // Get stored OTP for the phone number
        String storedOtp = otpStorage.get(phoneNumber);

        // Compare entered OTP with stored OTP
        if (storedOtp != null && storedOtp.equals(otpEntered)) {
            // Remove OTP from storage after successful verification
            otpStorage.remove(phoneNumber);
            return true;
        }
        return false;
    }

    /**
     * Verify OTP for user login.
     */
    public boolean verifyLoginOtp(String phoneNumber, String otpEntered) {
        return verifyOtp(phoneNumber, otpEntered, otpStorage);
    }

    /**
     * Verify OTP for forget password.
     */
    public boolean verifyOtp(String phoneNumber, String otpEntered) {
        return verifyOtp(phoneNumber, otpEntered, otpStorage);
    }

    /**
     * Update user's last login time.
     *
     * @param user the user to update
     */
    @Transactional
    public void updateUserLastLogin(UserDTO user) {
        UpdateUserInternalDTO updateUserInternalDTO = new UpdateUserInternalDTO();
        updateUserInternalDTO.setLastLogin(Timestamp.valueOf(LocalDateTime.now()));
        updateUserInternalDTO.setUserId(user.getUserId());
        setUserDetailsInternally(updateUserInternalDTO);
    }

    /**
     * Verify a user's email.
     *
     * @param email the email to verify
     * @throws CustomException if user not found
     */
    @Transactional
    public void verifyUser(String email) {
        UserDTO user = getUserDetails("email", email);
        if (user == null) {
            throw new CustomException("User not found");
        }
        user.setVerificationStatus("Verified");
        UpdateUserInternalDTO updateUserInternalDTO = new UpdateUserInternalDTO();
        updateUserInternalDTO.setStatus("Verified");
        updateUserInternalDTO.setUserId(user.getUserId());
        setUserDetailsInternally(updateUserInternalDTO);
    }

    public UserDTO getUserDetails(String key, String value) {
        String url = "http://localhost:8080/internal/user?" + key + "=" + value;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Request", "true");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<UserDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                UserDTO.class
        );

        return response.getBody();
    }

    public void setUserDetailsInternally(UpdateUserInternalDTO updateDto) {
        String url = "http://localhost:8080/internal/user/internal-update";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Request", "true");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UpdateUserInternalDTO> entity = new HttpEntity<>(updateDto, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
        );

        response.getBody();
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
        setUserDetailsInternally(updateUserInternalDTO);
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
    /**
     * Verify OTP for user registration.
     */
    public boolean verifyRegistrationOtp(String phoneNumber, String otpEntered) {
        boolean isValid = verifyOtp(phoneNumber, otpEntered, registrationOtpStorage);

        if (isValid) {
            // Update user verification status
            UserDTO user = getUserDetails("phoneNumber",phoneNumber);
            if (user != null) {
                UpdateUserInternalDTO updateUserInternalDTO=new UpdateUserInternalDTO();
                updateUserInternalDTO.setUserId(user.getUserId());
                updateUserInternalDTO.setVerificationStatus("Verified");
                setUserDetailsInternally(updateUserInternalDTO);
            }
        }
        return isValid;
    }
}