
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
            String firebaseConfig = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"${FIREBASE_PROJECT_ID}\",\n" +
                    "  \"private_key_id\": \"${FIREBASE_PRIVATE_KEY_ID}\",\n" +
                    "  \"private_key\": \"${FIREBASE_PRIVATE_KEY}\",\n" +
                    "  \"client_email\": \"${FIREBASE_CLIENT_EMAIL}\",\n" +
                    "  \"client_id\": \"${FIREBASE_CLIENT_ID}\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-fbsvc%40karyanest-275c3.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}";

            firebaseConfig = firebaseConfig.replace("${FIREBASE_PROJECT_ID}", System.getenv("FIREBASE_PROJECT_ID"));
            firebaseConfig = firebaseConfig.replace("${FIREBASE_PRIVATE_KEY_ID}", System.getenv("FIREBASE_PRIVATE_KEY_ID"));
            firebaseConfig = firebaseConfig.replace("${FIREBASE_PRIVATE_KEY}", System.getenv("FIREBASE_PRIVATE_KEY").replace("\\n", "\n"));
            firebaseConfig = firebaseConfig.replace("${FIREBASE_CLIENT_EMAIL}", System.getenv("FIREBASE_CLIENT_EMAIL"));
            firebaseConfig = firebaseConfig.replace("${FIREBASE_CLIENT_ID}", System.getenv("FIREBASE_CLIENT_ID"));

            GoogleCredentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(firebaseConfig.getBytes()));

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
