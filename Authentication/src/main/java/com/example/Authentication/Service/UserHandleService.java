package com.example.Authentication.Service;

import com.example.Authentication.DTO.UpdateUserInternalDTO;
import com.example.Authentication.DTO.UserDTO;
import com.example.Authentication.Model.UserInternalUpdateEntity;
import com.example.Authentication.Repositery.UserInternalUpdateRepository;
import com.example.module_b.ExceptionAndExceptionHandler.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class UserHandleService {
    @Autowired
    private RedisService redisService;

    @Autowired
    private UserInternalUpdateRepository userInternalUpdateRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserHandleService.class);
    public void blockUser(Long userId, String flag) {
        if ("Hard".equalsIgnoreCase(flag)) {
            // HARD BLOCK: block in DB + Redis
            UpdateUserInternalDTO updateDto = new UpdateUserInternalDTO();
            updateDto.setUserId(userId);
            updateDto.setStatus("Blocked");
            setUserDetailsInternally(updateDto);
            redisService.addToBlockedUsers(userId);
        } else if ("Soft".equalsIgnoreCase(flag)) {
            // SOFT BLOCK: only Redis
            redisService.addToBlockedUsers(userId);
        } else {
            throw new CustomException("Invalid flag. Allowed values are 'Hard' or 'Soft'");
        }
    }

    public void unblockUser(Long userId) {
        UpdateUserInternalDTO updateDto = new UpdateUserInternalDTO();
        updateDto.setUserId(userId);
        updateDto.setStatus("Active");
        setUserDetailsInternally(updateDto);
        redisService.removeFromBlockedUsers(userId);
    }

    public void setUserDetailsInternally(UpdateUserInternalDTO updateDto) {
        Optional<UserInternalUpdateEntity> optionalUser = userInternalUpdateRepository.findById(updateDto.getUserId());
        if (optionalUser.isPresent()) {
            UserInternalUpdateEntity user = optionalUser.get();

            if (updateDto.getStatus() != null) {
                user.setStatus(UserInternalUpdateEntity.UserStatus.valueOf(updateDto.getStatus()));
            }
            if (updateDto.getNewPassword() != null) {
                user.setPassword(updateDto.getNewPassword());
            }
            if (updateDto.getVerificationStatus() != null) {
                user.setVerificationStatus(UserInternalUpdateEntity.VerificationStatus.valueOf(updateDto.getVerificationStatus()));
            }
            if (updateDto.getLastLogin() != null) {
                user.setLastLogin(updateDto.getLastLogin());
            }

            userInternalUpdateRepository.save(user);
        } else {
            throw new RuntimeException("User not found with ID: " + updateDto.getUserId());
        }
    }
    @Transactional
    public void updatePassword(Long userId, String newPassword) {
        try {
            logger.debug("Attempting to update password for userId: {}", userId);

            if (!isValidPassword(newPassword)) {
                logger.warn("Password validation failed for userId: {}", userId);
                throw new CustomException("Password must be at least 8 characters long, contain one uppercase letter, and one number.");
            }

            UpdateUserInternalDTO updateUserInternalDTO = new UpdateUserInternalDTO();
            updateUserInternalDTO.setNewPassword(newPassword);
            updateUserInternalDTO.setUserId(userId);

            logger.info("Password updated successfully for userId: {}", userId);
        } catch (Exception e) {
            logger.error("Failed to update password for userId: {}, error: {}", userId, e.getMessage());
            throw e; // Re-throw to be caught by the controller
        }
    }

    private boolean isValidPassword(String password) {
        // Password must be at least 8 characters, include one uppercase and one digit
        return password != null
                && password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*\\d.*");
    }
    public UserDTO getUserDetails(String key, String value) {
        UserInternalUpdateEntity user = switch (key.toLowerCase()) {
            case "username" -> userInternalUpdateRepository.findByUsername(value);
            case "email" -> userInternalUpdateRepository.findByEmail(value);
            case "phonenumber" -> userInternalUpdateRepository.findByPhoneNumber(value);
            default -> throw new CustomException("Invalid key: " + key);
        };

        if (user == null) {
            throw new CustomException("User not found with " + key + ": " + value);
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setEmail(user.getEmail());
        userDTO.setUsername(user.getUsername());
        userDTO.setFullName(user.getFullName());
        userDTO.setPassword(user.getPassword());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        if (user.getRole() != null) {
            userDTO.setRole(user.getRole().getName());
            userDTO.setRoleId(user.getRole().getId());
        }
        if (user.getVerificationStatus() != null) {
            userDTO.setVerificationStatus(user.getVerificationStatus().toString());
        }
        if (user.getVerificationMethod() != null) {
            userDTO.setVerificationMethod(user.getVerificationMethod().toString());
        }
        if (user.getStatus() != null) {
            userDTO.setStatus(user.getStatus().toString());
        }

        return userDTO;
    }


}
