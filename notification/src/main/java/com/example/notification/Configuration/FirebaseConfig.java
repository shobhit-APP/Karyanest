
package com.example.notification.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;

@Configuration
public class FirebaseConfig {
    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @PostConstruct
    public void initialize() {
        try {
            String privateKey = System.getenv("FIREBASE_PRIVATE_KEY").replace("\\n", "\n");

            GoogleCredentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(
                    ("{\"type\":\"service_account\",\"project_id\":\"" + System.getenv("FIREBASE_PROJECT_ID") +
                            "\",\"private_key_id\":\"" + System.getenv("FIREBASE_PRIVATE_KEY_ID") +
                            "\",\"private_key\":\"" + privateKey +
                            "\",\"client_email\":\"" + System.getenv("FIREBASE_CLIENT_EMAIL") +
                            "\",\"client_id\":\"" + System.getenv("FIREBASE_CLIENT_ID") +
                            "\",\"auth_uri\":\"https://accounts.google.com/o/oauth2/auth\",\"token_uri\":\"https://oauth2.googleapis.com/token\",\"auth_provider_x509_cert_url\":\"https://www.googleapis.com/oauth2/v1/certs\",\"client_x509_cert_url\":\"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-fbsvc%40karyanest-275c3.iam.gserviceaccount.com\",\"universe_domain\":\"googleapis.com\"}")
                            .getBytes()));

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp app = FirebaseApp.initializeApp(options);
                log.info("Firebase initialized successfully");
            } else {
                log.info("Firebase already initialized");
            }
        } catch (Exception e) {
            log.error("Failed to initialize Firebase: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize Firebase: " + e.getMessage(), e);
        }
    }
}
