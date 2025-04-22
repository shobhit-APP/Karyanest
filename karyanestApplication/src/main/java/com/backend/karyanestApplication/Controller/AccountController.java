package com.backend.karyanestApplication.Controller;

import com.example.Authentication.Controller.AuthController;
import com.backend.karyanestApplication.DTO.UserPreferencesDTO;
import com.backend.karyanestApplication.DTO.UserRegistrationDTO;
import com.backend.karyanestApplication.DTO.UserResponseDTO;
import com.backend.karyanestApplication.Model.User;
//import com.example.Authentication.Service.JwtService;
import com.backend.karyanestApplication.Service.UserService;
import com.example.Authentication.Component.UserContext;
import com.example.Authentication.DTO.AuthResponseDTO;
import com.example.Authentication.DTO.UserDTO;
import com.example.Authentication.Service.Auth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for account-related operations.
 */
@RestController
@RequestMapping("/v1/users")
@Validated
@Tag(name = "User", description = "User operations")
public class AccountController {
    @Autowired
    private Auth auth;
    @Autowired
    private UserContext userContext;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(LeadsController.class);
    public AccountController(UserService userService) {
        this.userService = userService;
    }
    /**
     * Get all users.
     *
     * @return ResponseEntity with a list of all users
     */
    @Operation(summary = "Get all users", description = "Retrieve a list of all registered users")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('users_getAll')")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Get a user by ID.
     *
     * @param id The ID of the user
     * @return ResponseEntity with user details
     */
    @Operation(summary = "Get user by ID", description = "Retrieve user details by user ID")
    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_AGENT') or hasAuthority('users_getByID')")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
       UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_AGENT') or hasAuthority('users_getCurrent')")
    @GetMapping("/currentUser")
    public ResponseEntity<?> getUserDetails(HttpServletRequest request) {
        String username = userContext.getUsername(request);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
        }

        User user = userService.getUserByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }

        // ✅ Required User Details mapped to DTO
        UserResponseDTO userDTO = userService.mapToDTO(user);

        // ✅ JWT Response
        UserDTO dto = new UserDTO();
        System.out.println(user.getId());
        dto.setUserId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setPhoneNumber(user.getPhoneNumber());
        if (user.getRole() != null) {
            dto.setRoleId(user.getRole().getId());
            dto.setRole(user.getRole().getName());
        }
        dto.setStatus(user.getStatus() != null ? user.getStatus().name() : null);
        dto.setVerificationMethod(user.getVerificationMethod() != null ? user.getVerificationMethod().name() : null);
        dto.setVerificationStatus(user.getVerificationStatus() != null ? user.getVerificationStatus().name() : null);
        ResponseEntity<?> jwtResponseEntity = auth.generateAuthResponseForUser(dto);

        // ✅ JWT Response ki body extract karein
        Object jwtBody = jwtResponseEntity.getBody();

        // ✅ Ensure karein ki body `AuthResponseDTO` ka object hai
        if (!(jwtBody instanceof AuthResponseDTO authResponse)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Invalid response format from JWT service"));
        }

        // ✅ JWT Response ko Map me convert karein aur User DTO add karein
        // ✅ LinkedHashMap for ordered entries
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("user", userDTO);            // First item
        responseBody.put("role", authResponse.getRole());

      // ✅ Final Response return karein
        return ResponseEntity.ok(responseBody);
    }
    /**
     * Update user details.
     *
     * @param id The ID of the user
     * @param userDTO The updated user details
     * @return ResponseEntity with updated user details
     */
    @Operation(summary = "Update user details", description = "Update details of an existing user by user ID")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_AGENT') or hasAuthority('users_update')")
    @PutMapping("{id}")
    public ResponseEntity<UserRegistrationDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UserRegistrationDTO userDTO) {

        User updatedUser = userService.updateUser(id, userDTO);

        UserRegistrationDTO responseDTO = new UserRegistrationDTO(
                updatedUser.getFullName(),
                updatedUser.getEmail(),
                updatedUser.getPhoneNumber(),
                null,
                updatedUser.getProfilePicture(),
                updatedUser.getAddress(),
                updatedUser.getCity(),
                updatedUser.getState(),
                updatedUser.getCountry(),
                updatedUser.getPincode(),
                updatedUser.getPreferences(),
                updatedUser.getCreatedAt(),
                updatedUser.getUpdatedAt(),
                updatedUser.getVerificationMethod().name()
        );

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Delete (deactivate) a user by ID.
     *
     * @param id The ID of the user
     * @return ResponseEntity with a deactivation message
     */
    @Operation(summary = "Delete (deactivate) user by ID", description = "Deactivate a user by user ID")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_AGENT') or hasAuthority('users_delete')")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null) {
            user.setStatus(User.UserStatus.Deleted);
            userService.updateUserStatus(user);
            return ResponseEntity.ok("User successfully deactivated.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }
    /**
     * Handles user activation or updates user preferences based on the provided input.
     *
     * @param id The ID of the user to activate or update preferences.
     * @param action Optional query parameter to specify the action (e.g., "activate").
     * @param userPreferencesDTO Optional request body containing the user preferences to update.
     * @return ResponseEntity containing a success message or updated user data, or an error message if the request fails.
     */
    @Operation(summary = "Activate user or update preferences", description = "Activate a previously deactivated user by ID or update user preferences.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_AGENT') or hasAuthority('users_activate')")
    @PatchMapping("/{id}")
    public ResponseEntity<?> activateUser(
            @PathVariable Long id,
            @RequestParam(required = false) String action,
            @RequestBody(required = false) UserPreferencesDTO userPreferencesDTO) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        // Activate User
        if ("activate".equalsIgnoreCase(action)) {
            if (user.getStatus() == User.UserStatus.Deleted) {
                user.setStatus(User.UserStatus.Active);
                userService.updateUserStatus(user);
                return ResponseEntity.ok("User successfully activated.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is already active.");
            }
        }
        // Update Preferences
        if (userPreferencesDTO != null) {
            UserResponseDTO userResponseDTO = userService.updateUserPreferences(id, userPreferencesDTO);
            return ResponseEntity.ok(userResponseDTO);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request. Provide a valid action or preferences.");
    }
}
