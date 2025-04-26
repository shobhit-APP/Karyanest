package com.example.Authentication.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RedisService {
    private static final String BLOCKED_USERS_SET = "blockedUsersId";
    private final RedisTemplate<String, Long> redisTemplate;

    @Autowired
    public RedisService(RedisTemplate<String, Long> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addToBlockedUsers(Long userId) {
        redisTemplate.opsForSet().add(BLOCKED_USERS_SET, userId);
    }

    public void removeFromBlockedUsers(Long userId) {
        redisTemplate.opsForSet().remove(BLOCKED_USERS_SET, userId);
    }

    public boolean isUserBlocked(Long userId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(BLOCKED_USERS_SET, userId));
    }

    public long getBlockedUsersCount() {
        Long size = redisTemplate.opsForSet().size(BLOCKED_USERS_SET);
        return size != null ? size : 0;
    }

    public Set<Long> getAllBlockedUsers() {
        return redisTemplate.opsForSet().members(BLOCKED_USERS_SET);
    }
}
