package com.example.notification.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

@Configuration
public class FirebaseConfig {
    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @PostConstruct
    public void initialize() {
        try {
            // Get environment variables
            String privateKeyBase64 = System.getenv("FIREBASE_PRIVATE_KEY");
            String projectId = System.getenv("FIREBASE_PROJECT_ID");
            String privateKeyId = System.getenv("FIREBASE_PRIVATE_KEY_ID");
            String clientEmail = System.getenv("FIREBASE_CLIENT_EMAIL");
            String clientId = System.getenv("FIREBASE_CLIENT_ID");

            // Validate environment variables
            if (privateKeyBase64 == null || projectId == null || privateKeyId == null ||
                    clientEmail == null || clientId == null) {
                throw new IllegalArgumentException("One or more Firebase environment variables are not set");
            }

            // Decode Base64-encoded private key
            String privateKey = new String(Base64.getDecoder().decode(privateKeyBase64));

            // Construct JSON, escaping newlines in private_key
            String serviceAccountJson = String.format(
                    "{\"type\":\"service_account\",\"project_id\":\"%s\",\"private_key_id\":\"%s\",\"private_key\":\"%s\",\"client_email\":\"%s\",\"client_id\":\"%s\",\"auth_uri\":\"https://accounts.google.com/o/oauth2/auth\",\"token_uri\":\"https://oauth2.googleapis.com/token\",\"auth_provider_x509_cert_url\":\"https://www.googleapis.com/oauth2/v1/certs\",\"client_x509_cert_url\":\"https://www.googleapis.com/robot/v1/metadata/x509/%s\",\"universe_domain\":\"googleapis.com\"}",
                    projectId, privateKeyId, privateKey.replace("\n", "\\n"), clientEmail, clientId, clientEmail
            );

            // Log JSON for debugging (remove in production)
            log.debug("Service Account JSON: {}", serviceAccountJson);

            // Convert to InputStream
            ByteArrayInputStream serviceAccountStream = new ByteArrayInputStream(serviceAccountJson.getBytes("UTF-8"));

            // Load credentials
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream);

            // Initialize Firebase
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp app = FirebaseApp.initializeApp(options);
                log.info("Firebase initialized successfully");
            } else {
                log.info("Firebase already initialized");
            }
        } catch (IOException e) {
            log.error("Failed to initialize Firebase: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize Firebase: " + e.getMessage(), e);
        }
    }
}