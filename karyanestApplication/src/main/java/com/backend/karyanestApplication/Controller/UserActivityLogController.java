package com.backend.karyanestApplication.Controller;

import com.backend.karyanestApplication.DTO.UserActivityLogResponseDTO;
import com.backend.karyanestApplication.Service.UserActivityLogService;
import com.backend.karyanestApplication.Service.UserService;
import com.example.Authentication.Component.UserContext;
import com.example.Authentication.UTIL.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/user_activity")
@RequiredArgsConstructor
public class UserActivityLogController {

    private final UserActivityLogService activityLogService;
    private final UserService userService;
    private final UserContext userContext;
    private final JwtUtil jwtUtil;

    // Get current user's all activity logs
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/logs")
    public ResponseEntity<?> getCurrentUserActivityLogs(HttpServletRequest request) {
        try {
            String token = userContext.extractToken(request);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Authentication token not found"));
            }

            String username = jwtUtil.extractUsername(token);
            Long userId = userService.getUserIdByUsername(username);

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            List<UserActivityLogResponseDTO> logs = activityLogService.getUserActivityLogs(userId);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve activity logs", "details", e.getMessage()));
        }
    }

    // Get current user's activity logs filtered by activity type
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<?> getUserActivityLogsByType(
            HttpServletRequest request,
            @RequestParam("activityType") String activityType) {
        try {
            if (activityType == null || activityType.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Activity type is required"));
            }

            String token = userContext.extractToken(request);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Authentication token not found"));
            }

            String username = jwtUtil.extractUsername(token);
            Long userId = userService.getUserIdByUsername(username);

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            List<UserActivityLogResponseDTO> logs = activityLogService.getUserActivityLogsByType(userId, activityType);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve activity logs", "details", e.getMessage()));
        }
    }

    // Get recent logs of current user
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/logs/recent")
    public ResponseEntity<?> getRecentUserActivityLogs(
            HttpServletRequest request,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            String token = userContext.extractToken(request);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Authentication token not found"));
            }

            String username = jwtUtil.extractUsername(token);
            Long userId = userService.getUserIdByUsername(username);

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            List<UserActivityLogResponseDTO> logs = activityLogService.getRecentUserActivityLogs(userId, limit);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve activity logs", "details", e.getMessage()));
        }
    }

    // 🔒 Admin-only: View any user's logs by ID
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserActivityLogsByAdmin(@PathVariable Long id) {
        try {
            List<UserActivityLogResponseDTO> logs = activityLogService.getUserActivityLogs(id);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve activity logs", "details", e.getMessage()));
        }
    }
}
