package com.example.storageService.Service;


import com.backblaze.b2.client.B2StorageClient;
import com.backblaze.b2.client.B2StorageClientFactory;
import com.backblaze.b2.client.contentSources.B2ByteArrayContentSource;
import com.backblaze.b2.client.contentSources.B2ContentSource;
import com.backblaze.b2.client.exceptions.B2Exception;
import com.backblaze.b2.client.structures.*;
import com.example.storageService.Config.StorageProperties;
import com.example.storageService.Model.FileVersion;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class B2StorageService implements StorageService {

    private final StorageProperties properties;
    private final RedisTemplate<String, Object> storageServiceRedisTemplate;

    private String getTokenKey() {
        String bucketName = properties.getB2BucketName();
        if (bucketName == null) {
            throw new IllegalStateException("B2 bucket name is not configured");
        }
        return "b2:auth:token:" + bucketName;
    }

    private void validateCredentials() {
        if (properties.getB2KeyId() == null || properties.getB2AppKey() == null || properties.getB2BucketId() == null ||
                properties.getB2BucketName() == null) {
            throw new IllegalStateException("Backblaze B2 credentials are not configured");
        }
    }

    private String getFolderName(String context) {
        if ("avatar".equalsIgnoreCase(context)) {
            return properties.getB2AvatarsFolder();
        } else if ("property".equalsIgnoreCase(context)) {
            return properties.getB2PropertiesFolder();
        } else {
            throw new IllegalArgumentException("Invalid context: " + context);
        }
    }

    @Override
    public FileVersion uploadFile(String fileName, InputStream inputStream, long contentLength, String contentType, Long id, String context)
            throws IOException, B2Exception {
        validateCredentials();
        String folder = getFolderName(context);

        B2StorageClient client = B2StorageClientFactory
                .createDefaultFactory()
                .create(properties.getB2KeyId(), properties.getB2AppKey(), "storageService");

        String originalFileName = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
        String extension = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".") + 1) : "";
        String uniqueFileName = String.format("%s/%d_%s_%s.%s", folder, id, originalFileName, UUID.randomUUID().toString(), extension);

        byte[] fileBytes = inputStream.readAllBytes();
        B2ContentSource source = B2ByteArrayContentSource.build(fileBytes);
        B2UploadFileRequest request = B2UploadFileRequest
                .builder(properties.getB2BucketId(), uniqueFileName, contentType, source)
                .build();

        B2FileVersion fileVersion = client.uploadSmallFile(request);

        FileVersion response = new FileVersion();
        response.setFileId(fileVersion.getFileId());
        response.setFileName(properties.getB2BucketName() + "/" + uniqueFileName);

        return response;
    }

    @Override
    public void deleteFile(String fileName, String fileId) throws B2Exception {
        validateCredentials();

        B2StorageClient client = B2StorageClientFactory
                .createDefaultFactory()
                .create(properties.getB2KeyId(), properties.getB2AppKey(), "storageService");
        B2DeleteFileVersionRequest request = B2DeleteFileVersionRequest.builder(fileName, fileId).build();
        client.deleteFileVersion(request);
    }

    @Override
    public String getAuthToken() throws B2Exception {
        String tokenKey = getTokenKey();
        String cachedToken = (String) storageServiceRedisTemplate.opsForValue().get(tokenKey);
        if (cachedToken != null) {
            return cachedToken;
        }

        B2StorageClient client = B2StorageClientFactory
                .createDefaultFactory()
                .create(properties.getB2KeyId(), properties.getB2AppKey(), "storageService");
        B2GetDownloadAuthorizationRequest request = B2GetDownloadAuthorizationRequest
                .builder(properties.getB2BucketId(), "", (int) properties.getB2TokenTtlSeconds())
                .build();
        B2DownloadAuthorization auth = client.getDownloadAuthorization(request);
        String token = auth.getAuthorizationToken();
        storageServiceRedisTemplate.opsForValue().set(tokenKey, token, Duration.ofSeconds(properties.getB2TokenTtlSeconds()));
        return token;
    }
}
