package com.backend.karyanestApplication.Controller;

import com.backend.karyanestApplication.Model.User;
import com.backend.karyanestApplication.Service.UpdateUser;
import com.backend.karyanestApplication.Service.UserService;
import com.example.Authentication.DTO.UpdateUserInternalDTO;
import com.example.Authentication.DTO.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal")
public class InternalUserController {

    private static final Logger log = LoggerFactory.getLogger(InternalUserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UpdateUser updateUser;

    @GetMapping("/user")
    public ResponseEntity<?> getUser(
            @RequestHeader(value = "X-Internal-Request", required = false) String internalHeader,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(name = "userId", required = false) Long userId) {
        System.out.println(email);
        if (!"true".equalsIgnoreCase(internalHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: Internal requests only.");
        }

        User user = null;

        if (email != null) {
            log.info("Fetching user by email: {}", email);
            user = userService.getUserByEmail(email);
        } else if (username != null) {
            log.info("Fetching user by username: {}", username);
            user = userService.getUserByUsername(username);
        } else if (phoneNumber != null) {
            log.info("Fetching user by phone number: {}", phoneNumber);
            user = userService.findByPhoneNumber(phoneNumber);
        } else if (userId != null) {
            log.info("Fetching user by userId: {}", userId);
            user = userService.findById(userId);
        } else {
            return ResponseEntity.badRequest().body("Please provide email, username, phoneNumber or userId.");
        }

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        UserDTO dto = new UserDTO();
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

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/user/internal-update")
    public ResponseEntity<?> updateUserInternally(
            @RequestHeader(value = "X-Internal-Request", required = false) String internalHeader,
            @RequestBody UpdateUserInternalDTO dto) {

        if (!"true".equalsIgnoreCase(internalHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: Internal requests only.");
        }

        try {
            boolean updated = updateUser.updateUserInternally(dto);
            if (updated) {
                return ResponseEntity.ok("User updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
        } catch (IllegalArgumentException e) {
            log.error("Enum conversion failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Invalid enum value provided.");
        } catch (Exception e) {
            log.error("Internal error occurred: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating user.");
        }
    }
}
