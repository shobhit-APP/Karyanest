package com.example.storageService.Service;

import com.example.storageService.Config.StorageProperties;
import com.example.storageService.Model.FileVersion;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.UUID;

@RequiredArgsConstructor
public class R2StorageService implements StorageService {

    private final StorageProperties properties;
    private final RedisTemplate<String, Object> storageServiceRedisTemplate;
    private final HttpClient client = HttpClient.newHttpClient();

    private String getTokenKey() {
        String bucketName = properties.getR2BucketName();
        if (bucketName == null) {
            throw new IllegalStateException("R2 bucket name is not configured");
        }
        return "r2:auth:token:" + bucketName;
    }


    private String getFolderName(String context) {
        return switch (context.toLowerCase()) {
            case "avatar" -> properties.getR2AvatarsFolder();
            case "property" -> properties.getR2PropertiesFolder();
            default -> throw new IllegalArgumentException("Invalid context: " + context);
        };
    }

    @Override
    public FileVersion uploadFile(String fileName, InputStream inputStream, long contentLength, String contentType, Long id, String context)
            throws IOException, Exception {
        validateCredentials();
        String folder = getFolderName(context);

        String originalFileName = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
        String extension = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".") + 1) : "";
        String uniqueFileName = String.format("%s/%d_%s_%s.%s", folder, id, originalFileName, UUID.randomUUID().toString(), extension);

        byte[] fileBytes = inputStream.readAllBytes();
        String contentHash = calculateSha256(fileBytes);
        String date = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"));
        String canonicalRequest = createCanonicalRequest("PUT", uniqueFileName, contentHash, date);
        String signature = createSignature(canonicalRequest, date);

        String bucketName = properties.getR2BucketName();
        String endpoint = String.format("https://%s.r2.cloudflarestorage.com", properties.getR2AccountId());
        String requestUri = String.format("%s/%s/%s", endpoint, bucketName, uniqueFileName);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUri))
                .header("Authorization", String.format(
                        "AWS4-HMAC-SHA256 Credential=%s/%s/auto/s3/aws4_request, SignedHeaders=host;x-amz-content-sha256;x-amz-date, Signature=%s",
                        properties.getR2AccessKeyId(), date.substring(0, 8), signature))
                .header("x-amz-content-sha256", contentHash)
                .header("x-amz-date", date)
                .header("Content-Type", contentType != null ? contentType : "application/octet-stream")
                .PUT(HttpRequest.BodyPublishers.ofByteArray(fileBytes))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new IOException("Upload failed: " + response.body());
        }

        FileVersion fileVersion = new FileVersion();
        fileVersion.setFileId(uniqueFileName);
        fileVersion.setFileName(bucketName + "/" + uniqueFileName);

        return fileVersion;
    }

    @Override
    public void deleteFile(String fileName, String fileId) throws Exception {
        validateCredentials();
        String date = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"));
        String contentHash = calculateSha256(new byte[0]);
        String canonicalRequest = createCanonicalRequest("DELETE", fileName, contentHash, date);
        String signature = createSignature(canonicalRequest, date);

        String bucketName = properties.getR2BucketName();
        String endpoint = String.format("https://%s.r2.cloudflarestorage.com", properties.getR2AccountId());
        String requestUri = String.format("%s/%s/%s", endpoint, bucketName, fileName);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUri))
                .header("Authorization", String.format(
                        "AWS4-HMAC-SHA256 Credential=%s/%s/auto/s3/aws4_request, SignedHeaders=host;x-amz-content-sha256;x-amz-date, Signature=%s",
                        properties.getR2AccessKeyId(), date.substring(0, 8), signature))
                .header("x-amz-content-sha256", contentHash)
                .header("x-amz-date", date)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new IOException("Delete failed: " + response.body());
        }
    }

    @Override
    public String getAuthToken() throws Exception {
        String tokenKey = getTokenKey();
        String cachedToken = (String) storageServiceRedisTemplate.opsForValue().get(tokenKey);
        if (cachedToken != null) {
            return cachedToken;
        }
        String token = UUID.randomUUID().toString();
        storageServiceRedisTemplate.opsForValue().set(tokenKey, token, Duration.ofSeconds(3600));
        return token;
    }

    private void validateCredentials() {
        if (properties.getR2AccountId() == null || properties.getR2AccessKeyId() == null ||
                properties.getR2SecretAccessKey() == null || properties.getR2BucketName() == null) {
            throw new IllegalStateException("Cloudflare R2 credentials are not configured");
        }
    }

    private String calculateSha256(byte[] data) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            String hashHex = HexFormat.of().formatHex(hash);
            return hashHex;
        } catch (Exception e) {
            throw new IOException("Failed to calculate SHA-256", e);
        }
    }

    private String createCanonicalRequest(String method, String fileName, String contentHash, String date) {
        String bucketName = properties.getR2BucketName();
        String canonicalRequest = String.format(
                "%s\n/%s/%s\n\nhost:%s.r2.cloudflarestorage.com\nx-amz-content-sha256:%s\nx-amz-date:%s\n\nhost;x-amz-content-sha256;x-amz-date\n%s",
                method, bucketName, fileName, properties.getR2AccountId(), contentHash, date, contentHash);
        return canonicalRequest;
    }

    private String createSignature(String canonicalRequest, String date) throws Exception {
        String dateStamp = date.substring(0, 8);
        String scope = String.format("%s/auto/s3/aws4_request", dateStamp);
        String stringToSign = String.format(
                "AWS4-HMAC-SHA256\n%s\n%s\n%s",
                date, scope, calculateSha256(canonicalRequest.getBytes())
        );

        byte[] dateKey = hmacSha256(("AWS4" + properties.getR2SecretAccessKey()).getBytes(), dateStamp);
        byte[] dateRegionKey = hmacSha256(dateKey, "auto");
        byte[] dateServiceKey = hmacSha256(dateRegionKey, "s3");
        byte[] signingKey = hmacSha256(dateServiceKey, "aws4_request");
        String signature = HexFormat.of().formatHex(hmacSha256(signingKey, stringToSign));
        return signature;
    }

    private byte[] hmacSha256(byte[] key, String data) throws Exception {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            byte[] result = mac.doFinal(data.getBytes());
            return result;
        } catch (Exception e) {
            throw e;
        }
    }

//    private final StorageProperties properties;
//    private final RedisTemplate<String, Object> storageServiceRedisTemplate;
//    private final HttpClient client = HttpClient.newHttpClient();
//    private static final Logger logger = LoggerFactory.getLogger(R2StorageService.class);
//
//    private String getTokenKey() {
//        String bucketName = properties.getR2BucketName();
//        if (bucketName == null) {
//            logger.error("R2 bucket name is null");
//            throw new IllegalStateException("R2 bucket name is not configured");
//        }
//        return "r2:auth:token:" + bucketName;
//    }
//
//    @Override
//    public FileVersion uploadPropertyFile(String fileName, InputStream inputStream, long contentLength, String contentType, Long propertyId)
//            throws IOException, Exception {
//        validateR2Credentials();
//        logger.debug("Uploading property file: fileName={}, propertyId={}", fileName, propertyId);
//        return uploadFile(fileName, inputStream, contentLength, contentType, propertyId, properties.getR2PropertiesFolder());
//    }
//
//    @Override
//    public FileVersion uploadAvatarFile(String fileName, InputStream inputStream, long contentLength, String contentType, Long userId)
//            throws IOException, Exception {
//        validateR2Credentials();
//        logger.debug("Uploading avatar file: fileName={}, userId={}", fileName, userId);
//        try {
//            return uploadFile(fileName, inputStream, contentLength, contentType, userId, properties.getR2AvatarsFolder());
//        } catch (Exception e) {
//            logger.error("Failed to upload avatar file: fileName={}, userId={}, error={}", fileName, userId, e.getMessage(), e);
//            throw e;
//        }
//    }
//
//    private FileVersion uploadFile(String fileName, InputStream inputStream, long contentLength, String contentType, Long id, String folder)
//            throws IOException, Exception {
//        logger.debug("Preparing to upload file: fileName={}, contentLength={}, folder={}", fileName, contentLength, folder);
//        String originalFileName = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
//        String extension = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".") + 1) : "";
//        String uniqueFileName = String.format("%s/%d_%s_%s.%s", folder, id, originalFileName, UUID.randomUUID().toString(), extension);
//        logger.debug("Generated unique file name: {}", uniqueFileName);
//
//        byte[] fileBytes = inputStream.readAllBytes();
//        logger.debug("Read file bytes: length={}", fileBytes.length);
//        String contentHash = calculateSha256(fileBytes);
//        String date = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"));
//        String canonicalRequest = createCanonicalRequest("PUT", uniqueFileName, contentHash, date);
//        String signature = createSignature(canonicalRequest, date);
//
//        String endpoint = String.format("https://%s.r2.cloudflarestorage.com", properties.getR2AccountId());
//        logger.debug("R2 endpoint: {}", endpoint);
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(String.format("%s/%s/%s", endpoint, properties.getR2BucketName(), uniqueFileName)))
//                .header("Authorization", String.format(
//                        "AWS4-HMAC-SHA256 Credential=%s/%s/auto/s3/aws4_request, SignedHeaders=host;x-amz-content-sha256;x-amz-date, Signature=%s",
//                        properties.getR2AccessKeyId(), date.substring(0, 8), signature))
//                .header("x-amz-content-sha256", contentHash)
//                .header("x-amz-date", date)
//                .header("Content-Type", contentType != null ? contentType : "application/octet-stream")
//                .PUT(HttpRequest.BodyPublishers.ofByteArray(fileBytes))
//                .build();
//
//        logger.debug("Sending R2 upload request: uri={}, headers={}", request.uri(), request.headers());
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        if (response.statusCode() >= 400) {
//            logger.error("R2 upload failed: status={}, response={}", response.statusCode(), response.body());
//            throw new IOException("Upload failed: " + response.body());
//        }
//        logger.info("Successfully uploaded file to R2: bucket={}, fileName={}", properties.getR2BucketName(), uniqueFileName);
//
//        FileVersion fileVersion = new FileVersion();
//        fileVersion.setFileId(uniqueFileName);
//        fileVersion.setFileName(String.format("%s/%s/%s", endpoint, properties.getR2BucketName(), uniqueFileName));
//        logger.debug("Upload result: fileId={}, fileName={}", fileVersion.getFileId(), fileVersion.getFileName());
//
//        return fileVersion;
//    }
//
//    @Override
//    public void deleteFile(String fileName, String fileId) throws Exception {
//        validateR2Credentials();
//        logger.debug("Deleting file: fileName={}, fileId={}", fileName, fileId);
//        String date = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"));
//        String contentHash = calculateSha256(new byte[0]);
//        String canonicalRequest = createCanonicalRequest("DELETE", fileName, contentHash, date);
//        String signature = createSignature(canonicalRequest, date);
//
//        String endpoint = String.format("https://%s.r2.cloudflarestorage.com", properties.getR2AccountId());
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(String.format("%s/%s/%s", endpoint, properties.getR2BucketName(), fileName)))
//                .header("Authorization", String.format(
//                        "AWS4-HMAC-SHA256 Credential=%s/%s/auto/s3/aws4_request, SignedHeaders=host;x-amz-content-sha256;x-amz-date, Signature=%s",
//                        properties.getR2AccessKeyId(), date.substring(0, 8), signature))
//                .header("x-amz-content-sha256", contentHash)
//                .header("x-amz-date", date)
//                .DELETE()
//                .build();
//
//        logger.debug("Sending R2 delete request: uri={}, headers={}", request.uri(), request.headers());
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        if (response.statusCode() >= 400) {
//            logger.error("R2 delete failed: status={}, response={}", response.statusCode(), response.body());
//            throw new IOException("Delete failed: " + response.body());
//        }
//        logger.info("Successfully deleted file from R2: fileName={}", fileName);
//    }
//
//    @Override
//    public String getAuthToken() throws Exception {
//        String tokenKey = getTokenKey();
//        String cachedToken = (String) storageServiceRedisTemplate.opsForValue().get(tokenKey);
//        if (cachedToken != null) {
//            logger.debug("Returning cached R2 auth token: {}", cachedToken);
//            return cachedToken;
//        }
//        String token = UUID.randomUUID().toString();
//        logger.warn("Using dummy R2 auth token (placeholder for testing): {}", token);
//        storageServiceRedisTemplate.opsForValue().set(tokenKey, token, Duration.ofSeconds(3600));
//        return token;
//    }
//
//    private void validateR2Credentials() {
//        if (properties.getR2AccountId() == null || properties.getR2AccessKeyId() == null || properties.getR2SecretAccessKey() == null ||
//                properties.getR2BucketName() == null) {
//            logger.error("R2 credentials are missing: accountId={}, accessKeyId={}, bucketName={}",
//                    properties.getR2AccountId(), properties.getR2AccessKeyId(), properties.getR2BucketName());
//            throw new IllegalStateException("Cloudflare R2 credentials are not configured");
//        }
//        logger.debug("R2 credentials validated: accountId={}, accessKeyId={}, bucketName={}",
//                properties.getR2AccountId(), properties.getR2AccessKeyId(), properties.getR2BucketName());
//    }
//
//    private String calculateSha256(byte[] data) throws IOException {
//        try {
//            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//            byte[] hash = digest.digest(data);
//            return HexFormat.of().formatHex(hash);
//        } catch (Exception e) {
//            logger.error("Failed to calculate SHA-256: {}", e.getMessage(), e);
//            throw new IOException("Failed to calculate SHA-256", e);
//        }
//    }
//
//    private String createCanonicalRequest(String method, String fileName, String contentHash, String date) {
//        String canonicalRequest = String.format(
//                "%s\n/%s/%s\n\nhost:%s.r2.cloudflarestorage.com\nx-amz-content-sha256:%s\nx-amz-date:%s\n\nhost;x-amz-content-sha256;x-amz-date\n%s",
//                method, properties.getR2BucketName(), fileName, properties.getR2AccountId(), contentHash, date, contentHash);
//        logger.debug("Canonical request: {}", canonicalRequest);
//        return canonicalRequest;
//    }
//
//    private String createSignature(String canonicalRequest, String date) throws Exception {
//        String dateStamp = date.substring(0, 8);
//        String scope = String.format("%s/auto/s3/aws4_request", dateStamp);
//        String stringToSign = String.format(
//                "AWS4-HMAC-SHA256\n%s\n%s\n%s",
//                date, scope, calculateSha256(canonicalRequest.getBytes())
//        );
//        logger.debug("String to sign: {}", stringToSign);
//
//        byte[] dateKey = hmacSha256(("AWS4" + properties.getR2SecretAccessKey()).getBytes(), dateStamp);
//        byte[] dateRegionKey = hmacSha256(dateKey, "auto");
//        byte[] dateServiceKey = hmacSha256(dateRegionKey, "s3");
//        byte[] signingKey = hmacSha256(dateServiceKey, "aws4_request");
//        String signature = HexFormat.of().formatHex(hmacSha256(signingKey, stringToSign));
//        logger.debug("Generated signature: {}", signature);
//
//        return signature;
//    }
//
//    private byte[] hmacSha256(byte[] key, String data) throws Exception {
//        try {
//            Mac mac = Mac.getInstance("HmacSHA256");
//            mac.init(new SecretKeySpec(key, "HmacSHA256"));
//            return mac.doFinal(data.getBytes());
//        } catch (Exception e) {
//            logger.error("Failed to compute HMAC-SHA256: {}", e.getMessage(), e);
//            throw e;
//        }
//    }
}
