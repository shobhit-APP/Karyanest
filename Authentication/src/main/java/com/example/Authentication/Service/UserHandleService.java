package com.example.Authentication.Service;

import com.example.Authentication.DTO.UpdateUserInternalDTO;
import com.example.Authentication.Model.UserInternalUpdateEntity;
import com.example.Authentication.Repositery.UserInternalUpdateRepository;
import com.example.module_b.ExceptionAndExceptionHandler.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class UserHandleService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RedisService redisService;

    @Autowired
    private UserInternalUpdateRepository userInternalUpdateRepository;

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

    private void setUserDetailsInternally(UpdateUserInternalDTO updateDto) {
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
}
