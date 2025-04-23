package com.backend.karyanestApplication.Service;

import com.example.Authentication.DTO.JWTUserDTO;
import com.backend.karyanestApplication.DTO.UserActivityLogResponseDTO;
import com.backend.karyanestApplication.Model.User;
import com.backend.karyanestApplication.Model.UserActivityLog;
import com.backend.karyanestApplication.Repositry.UserActivityLogRepo;
import com.backend.karyanestApplication.Repositry.UserRepo;  // UserRepo sirf reference banane ke liye
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserActivityLogService {

    @Autowired
    private UserActivityLogRepo userActivityLogRepo;

    @Autowired
    private UserRepo userRepo;

    @Transactional
    public void logActivity(String activityType, String description, String location, User user) {
        UserActivityLog log = new UserActivityLog();
        log.setUserId(user.getId());
        log.setActivityType(activityType);
        log.setDescription(description);
        log.setLocation(location);
        userActivityLogRepo.save(log);
    }
    @Transactional
    public void logActivityUsingJWTDTO(String activityType, String description, String location, JWTUserDTO jwtUserDTO) {
        UserActivityLog log = new UserActivityLog();
        log.setUserId(jwtUserDTO.getUserId());
        log.setActivityType(activityType);
        log.setDescription(description);
        log.setLocation(location);
        userActivityLogRepo.save(log);
    }


    @Transactional
    public void logActivityUsingJWTDTTO(String activityType, String description, String location, User user) {

        UserActivityLog log = new UserActivityLog();
        log.setUserId(user.getId());
        log.setActivityType(activityType);
        log.setDescription(description);
        log.setLocation(location);
        userActivityLogRepo.save(log);
    }
    public List<UserActivityLogResponseDTO> getUserActivityLogs(Long userId) {
        List<UserActivityLog> activityLogs = userActivityLogRepo.findByUserId(userId);
        return mapToActivityLogDTOList(activityLogs);
    }

    public List<UserActivityLogResponseDTO> getUserActivityLogsByType(Long userId, String activityType) {
        List<UserActivityLog> activityLogs = userActivityLogRepo.findByUserIdAndActivityTypeOrderByCreatedAtDesc(userId, activityType);
        return mapToActivityLogDTOList(activityLogs);
    }

    // Then in your service:
    public List<UserActivityLogResponseDTO> getRecentUserActivityLogs(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<UserActivityLog> activityLogs = userActivityLogRepo.findRecentByUserId(userId, pageable);
        return mapToActivityLogDTOList(activityLogs);
    }

    public UserActivityLogResponseDTO mapToActivityLogDTO(UserActivityLog activityLog) {
        UserActivityLogResponseDTO dto = new UserActivityLogResponseDTO();
        dto.setId(activityLog.getId());
        dto.setUserId(activityLog.getUserId());
        User user = userRepo.getUserById(activityLog.getUserId());
        dto.setUsername(user == null ? null : user.getUsername());
        dto.setFullname(user == null ? null : user.getFullName());
        dto.setActivityType(activityLog.getActivityType());
        dto.setDescription(activityLog.getDescription());
        dto.setCreatedAt(activityLog.getCreatedAt());
        dto.setLocation(activityLog.getLocation());
        return dto;
    }

    public List<UserActivityLogResponseDTO> mapToActivityLogDTOList(List<UserActivityLog> activityLogs) {
        return activityLogs.stream()
                .map(this::mapToActivityLogDTO)
                .collect(Collectors.toList());
    }
}
