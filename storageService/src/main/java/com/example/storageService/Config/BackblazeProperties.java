package com.example.storageService.Config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "backblaze")
@Data
public class BackblazeProperties {
    private String keyId;
    private String appKey;
    private String bucketId;
    private String bucketName;
}
