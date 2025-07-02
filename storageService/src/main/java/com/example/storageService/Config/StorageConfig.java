package com.example.storageService.Config;

import com.example.storageService.Service.DynamicStorageService;
import com.example.storageService.Service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
//
@Configuration
public class StorageConfig {
//    @Bean
//    public StorageService storageService(
//            StorageProperties properties,
//            RedisTemplate<String, Object> storageServiceRedisTemplate) {
//        if ("r2".equalsIgnoreCase(properties.getProvider())) {
//            return new R2StorageService(properties, storageServiceRedisTemplate);
//        } else {
//            return new B2StorageService(properties, storageServiceRedisTemplate);
//        }
//    }
//}

@Bean
public B2StorageService b2StorageService(StorageProperties properties, RedisTemplate<String, Object> redisTemplate) {
    return new B2StorageService(properties, redisTemplate);
}

@Bean
public R2StorageService r2StorageService(StorageProperties properties, RedisTemplate<String, Object> redisTemplate) {
    return new R2StorageService(properties, redisTemplate);
}

@Bean
public DynamicStorageService dynamicStorageService(
        B2StorageService b2StorageService,
        R2StorageService r2StorageService) {
    return new DynamicStorageService(b2StorageService, r2StorageService);
}
}
