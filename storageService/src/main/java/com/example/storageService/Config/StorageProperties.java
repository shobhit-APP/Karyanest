package com.example.storageService.Config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "storage")
@Data
public class StorageProperties {
//    private String keyId;
//    private String appKey;
//    private String bucketId;
//    private String bucketName;
    private String provider;
    private String b2KeyId;
    private String b2AppKey;
    private String b2BucketId;
    private String b2BucketName;
    private String b2ApiUrl;
    private long b2TokenTtlSeconds;
    private String b2PropertiesFolder;
    private String b2AvatarsFolder;
    private String r2AccountId;
    private String r2AccessKeyId;
    private String r2SecretAccessKey;
    private String r2BucketName;
    private String r2PropertiesFolder;
    private String r2AvatarsFolder;

}
