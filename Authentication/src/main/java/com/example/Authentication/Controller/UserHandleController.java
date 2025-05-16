package com.example.Authentication.Controller;

import com.example.Authentication.DTO.UserDTO;
import com.example.Authentication.Service.Auth;
import com.example.Authentication.Service.RedisService;
import com.example.Authentication.Service.UserHandleService;
import com.example.module_b.ExceptionAndExceptionHandler.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/v1/handle")
public class UserHandleController {

    @Autowired
    private Auth auth;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserHandleService userHandleService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/block")
    public ResponseEntity<?> blockUser(@RequestBody Map<String, String> request) {
        Long userId = Long.valueOf(request.get("userId"));
        String flag = request.get("flag"); // get a flag from request

        if (userId == null || flag == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User ID and flag are required"));
        }

        try {
            userHandleService.blockUser(userId, flag); // pass a flag also
            return ResponseEntity.ok().body(Map.of("message", "User blocked successfully"));
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/unblock")
    public ResponseEntity<?> unblockUser(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User ID is required"));
        }
        try {
            userHandleService.unblockUser(userId);
            return ResponseEntity.ok().body(Map.of("message", "User unblocked successfully"));
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/check-blocked")
    public ResponseEntity<?> checkBlockedUser(@RequestBody Map<String, String> request) {
        String key = request.get("key");
        String value = request.get("value");

        if (key == null || value == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Key and value are required"));
        }

        UserDTO user = auth.getUserDetails(key, value);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }

        boolean isBlockedInRedis = redisService.isUserBlocked(user.getUserId());
        String dbStatus = user.getStatus();

        return ResponseEntity.ok().body(Map.of(
                "userId", user.getUserId(),
                "isBlockedInRedis", isBlockedInRedis,
                "databaseStatus", dbStatus
        ));
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/blocked-users/count")
    public ResponseEntity<?> getBlockedUsersCount() {
        long count = redisService.getBlockedUsersCount();
        Set<Long> blockedUsers = redisService.getAllBlockedUsers();
        return ResponseEntity.ok().body(Map.of(
                "blockedUsersCount", count,
                "blockedUsers", blockedUsers
        ));
    }
}
