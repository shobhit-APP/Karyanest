package com.example.notification.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @PostConstruct
    public void initialize() {
        try (InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-adminsdk.json")) {
            if (serviceAccount == null) {
                throw new RuntimeException("firebase-adminsdk.json not found in classpath");
            }

            // Load credentials
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
            String projectId = null;

            // Attempt to cast to ServiceAccountCredentials to get projectId
            if (credentials instanceof ServiceAccountCredentials) {
                ServiceAccountCredentials sac = (ServiceAccountCredentials) credentials;
                projectId = sac.getProjectId();
                if (projectId == null) {
                    log.warn("Project ID not found in ServiceAccountCredentials, using fallback");
                } else {
                    log.info("Project ID extracted from credentials: {}", projectId);
                }
            } else {
                log.warn("Credentials are not ServiceAccountCredentials, projectId extraction skipped");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .setProjectId(projectId) // Explicitly set projectId
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp app = FirebaseApp.initializeApp(options);
                log.info("Firebase initialized successfully for project: {}", app.getOptions().getProjectId());
            } else {
                log.info("Firebase already initialized, project: {}", FirebaseApp.getInstance().getOptions().getProjectId());
            }
        } catch (Exception e) {
            log.error("Failed to initialize Firebase: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize Firebase: " + e.getMessage(), e);
        }
    }
}
