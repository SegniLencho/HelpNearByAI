package com.helpnearby.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.service.account.json}")
    private String firebaseServiceAccountJson;


    @PostConstruct
    public void init() {
        if (!FirebaseApp.getApps().isEmpty()) {
            logger.info("Firebase Admin SDK already initialized");
            return;
        }

        if (firebaseServiceAccountJson == null || firebaseServiceAccountJson.isBlank()) {
            throw new IllegalStateException("Firebase service account JSON is missing. Please set FIREBASE_SERVICE_ACCOUNT environment variable.");
        }

        try {
            byte[] decoded = Base64.getDecoder().decode(firebaseServiceAccountJson);
            InputStream serviceAccount = new ByteArrayInputStream(decoded);

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            FirebaseApp.initializeApp(options);
            logger.info("Firebase Admin SDK initialized successfully");
        } catch (IllegalArgumentException e) {
            logger.error("Invalid Base64 encoded Firebase service account JSON", e);
            throw new IllegalStateException("Failed to decode Firebase service account JSON. Make sure FIREBASE_SERVICE_ACCOUNT is Base64 encoded.", e);
        } catch (Exception e) {
            logger.error("Firebase initialization failed", e);
            throw new IllegalStateException("Firebase init failed: " + e.getMessage(), e);
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        return FirebaseMessaging.getInstance();
    }
}
