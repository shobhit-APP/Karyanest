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
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
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
    private static final Logger logger = LoggerFactory.getLogger(R2StorageService.class);


    private String getTokenKey() {
        String bucketName = properties.getR2BucketName();
        if (bucketName == null) {
            throw new IllegalStateException("R2 bucket name is not configured");
        }
        return "r2:auth:token:" + bucketName;
    }

    private String getFolderName(String context) {
        System.out.println("Context: " + context);

        return switch (context.toLowerCase()) {
            case "avatar" -> properties.getR2AvatarsFolder();
            case "property" -> properties.getR2PropertiesFolder();
            case "document" -> "propertydocuments";
            case "video" -> "propertyvideos";
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
    String cleanFileName = fileName.startsWith("/") ? fileName.substring(1) : fileName;
    // Encode once
    String encodedFileName = URLEncoder.encode(cleanFileName, StandardCharsets.UTF_8)
            .replace("+", "%20")
            .replace("%2F", "/");

// Use encoded in both CanonicalRequest and requestUri

    String bucketName = properties.getR2BucketName();
    String endpoint = String.format("https://%s.r2.cloudflarestorage.com", properties.getR2AccountId());
    String canonicalRequest = createCanonicalRequest("DELETE", encodedFileName, contentHash, date);

    String signature = createSignature(canonicalRequest, date);
    String requestUri = String.format("%s/%s/%s", endpoint, bucketName, encodedFileName);
    logger.info("Attempting to DELETE from R2: {}", requestUri);

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
        logger.error("Failed to delete from R2 | Status: {}, Response: {}", response.statusCode(), response.body());
        throw new IOException("Delete failed: " + response.body());
    }

    logger.info("✅ Successfully deleted file '{}' from R2 bucket '{}'", fileName, bucketName);
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

//    private String createCanonicalRequest(String method, String fileName, String contentHash, String date) {
//        String bucketName = properties.getR2BucketName();
//        String canonicalRequest = String.format(
//                "%s\n/%s/%s\n\nhost:%s.r2.cloudflarestorage.com\nx-amz-content-sha256:%s\nx-amz-date:%s\n\nhost;x-amz-content-sha256;x-amz-date\n%s",
//                method, bucketName, fileName, properties.getR2AccountId(), contentHash, date, contentHash);
//        return canonicalRequest;
//    }


    private String createCanonicalRequest(String method, String fileName, String contentHash, String date) {
        String bucketName = properties.getR2BucketName();

        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replace("+", "%20")
                .replace("%2F", "/"); // Allow actual slashes in the path

        return String.format(
                "%s\n/%s/%s\n\nhost:%s.r2.cloudflarestorage.com\nx-amz-content-sha256:%s\nx-amz-date:%s\n\nhost;x-amz-content-sha256;x-amz-date\n%s",
                method, bucketName, encodedFileName, properties.getR2AccountId(), contentHash, date, contentHash);
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
}
