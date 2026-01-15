package com.helpnearby.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

	private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

	@Value("${firebase.service-account.path:}")
	private String serviceAccountPath;

	@Value("${firebase.service-account.classpath:helpnearby-f7498-firebase-adminsdk-fbsvc-752449e7d7.json}")
	private String classpathServiceAccount;

	@PostConstruct
	public void initializeFirebase() throws IOException {
		if (FirebaseApp.getApps().isEmpty()) {
			InputStream serviceAccount = getServiceAccountInputStream();
			
			FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount))
					.build();

			FirebaseApp.initializeApp(options);
			logger.info("Firebase Admin SDK initialized successfully");
		} else {
			logger.info("Firebase Admin SDK already initialized");
		}
	}

	private InputStream getServiceAccountInputStream() throws IOException {
		// First, try to use the file path from properties
		if (serviceAccountPath != null && !serviceAccountPath.isEmpty()) {
			try {
				logger.info("Loading Firebase service account from file path: {}", serviceAccountPath);
				return new FileInputStream(serviceAccountPath);
			} catch (IOException e) {
				logger.warn("Failed to load Firebase service account from file path: {}", serviceAccountPath, e);
				// If file path doesn't work, fall back to classpath
			}
		}
		
		// Try to load from classpath
		ClassPathResource resource = new ClassPathResource(classpathServiceAccount);
		if (resource.exists()) {
			logger.info("Loading Firebase service account from classpath: {}", classpathServiceAccount);
			return resource.getInputStream();
		}
		
		// If neither works, try default location
		logger.warn("Service account file '{}' not found in classpath, trying default location", classpathServiceAccount);
		ClassPathResource defaultResource = new ClassPathResource("helpnearby-f7498-firebase-adminsdk-fbsvc-752449e7d7.json");
		if (defaultResource.exists()) {
			logger.info("Loading Firebase service account from default location");
			return defaultResource.getInputStream();
		}
		
		throw new IOException("Firebase service account file not found. Please ensure the file 'helpnearby-f7498-firebase-adminsdk-fbsvc-752449e7d7.json' is in src/main/resources/ or configure the path in application.properties");
	}

	@Bean
	public FirebaseMessaging firebaseMessaging() {
		return FirebaseMessaging.getInstance();
	}
}
