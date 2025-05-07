package com.example.storageService.Config;

import com.example.storageService.Service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class StorageConfig {
    @Bean
    public StorageService storageService(
            StorageProperties properties,
            RedisTemplate<String, Object> storageServiceRedisTemplate) {
        if ("r2".equalsIgnoreCase(properties.getProvider())) {
            return new R2StorageService(properties, storageServiceRedisTemplate);
        } else {
            return new B2StorageService(properties, storageServiceRedisTemplate);
        }
    }
}
