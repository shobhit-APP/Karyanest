package com.example.storageService.Service;


import com.backblaze.b2.client.B2StorageClient;
import com.backblaze.b2.client.B2StorageClientFactory;
import com.backblaze.b2.client.contentSources.B2ByteArrayContentSource;
import com.backblaze.b2.client.contentSources.B2ContentSource;
import com.backblaze.b2.client.exceptions.B2Exception;
import com.backblaze.b2.client.structures.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

@Service
public class B2FileService {

    @Value("${b2.bucket.name}")
    private String bucketName;

    @Value("${b2.bucket.id}")
    private String bucketId;

    @Value("${b2.api-url}")
    private String apiUrl;

    @Value("${b2.key-id}")
    private String keyId;

    @Value("${b2.application-key}")
    private String applicationKey;

    @Value("${b2.token-ttl-seconds}")
    private long tokenTtlSeconds;

    @Value("${b2.folders.properties}")
    private String propertiesFolder;

    @Value("${b2.folders.avatars}")
    private String avatarsFolder;


    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;
    public B2FileService(@Qualifier("storageServiceRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String getTokenKey() {
        return "b2:auth:token:" + bucketName;
    }

    private B2StorageClient getB2Client() throws B2Exception {
        return B2StorageClientFactory
                .createDefaultFactory()
                .create(keyId, applicationKey, "storageService");
    }

    public com.example.storageService.Model.B2FileVersion uploadPropertyFile(
            String fileName,
            InputStream inputStream,
            long contentLength,
            String contentType,
            Long propertyId) throws B2Exception {
        return uploadFile(fileName, inputStream, contentLength, contentType, propertyId, propertiesFolder);
    }

    public com.example.storageService.Model.B2FileVersion uploadAvatarFile(
            String fileName,
            InputStream inputStream,
            long contentLength,
            String contentType,
            Long userId) throws B2Exception {
        return uploadFile(fileName, inputStream, contentLength, contentType, userId, avatarsFolder);
    }

    private com.example.storageService.Model.B2FileVersion uploadFile(
            String fileName,
            InputStream inputStream,
            long contentLength,
            String contentType,
            Long id,
            String folder) throws B2Exception {

        B2StorageClient client = getB2Client();

        // Extract original filename (without extension) and extension
        String originalFileName = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
        String extension = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".") + 1) : "";

        // Generate unique filename: folder/id_originalFileName_uuid.extension
        String uniqueFileName = String.format("%s/%d_%s_%s.%s", folder, id, originalFileName, UUID.randomUUID().toString(), extension);

        byte[] fileBytes;
        try {
            fileBytes = inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read input stream", e);
        }

        B2ContentSource source = B2ByteArrayContentSource.build(fileBytes);
        B2UploadFileRequest request = B2UploadFileRequest
                .builder(bucketId, uniqueFileName, contentType, source)
                .build();

        com.backblaze.b2.client.structures.B2FileVersion fileVersion = client.uploadSmallFile(request);

        com.example.storageService.Model.B2FileVersion response = new com.example.storageService.Model.B2FileVersion();
        response.setFileId(fileVersion.getFileId());
        response.setFileName(String.format("%s/%s/%s",apiUrl, bucketName, uniqueFileName));

        return response;
    }

    public void deleteFile(String fileName, String fileId) throws B2Exception {
        B2StorageClient client = getB2Client();
        B2DeleteFileVersionRequest request = B2DeleteFileVersionRequest.builder(fileName, fileId).build();
        client.deleteFileVersion(request);
    }

    public String getAuthToken() throws B2Exception {
        String tokenKey = getTokenKey();
        String cachedToken = (String) redisTemplate.opsForValue().get(tokenKey);
        if (cachedToken != null) {
            return cachedToken;
        }
        B2StorageClient client = getB2Client();
        B2GetDownloadAuthorizationRequest request = B2GetDownloadAuthorizationRequest
                .builder(bucketId, "", (int) tokenTtlSeconds)
                .build();
        B2DownloadAuthorization auth = client.getDownloadAuthorization(request);
        String token = auth.getAuthorizationToken();
        redisTemplate.opsForValue().set(tokenKey, token, Duration.ofSeconds(tokenTtlSeconds));
        return token;
    }
}
