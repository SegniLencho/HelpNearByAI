package com.helpnearby.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Verifies that Jackson is properly configured with JavaTimeModule on application startup
 */
@Component
public class JacksonConfigVerifier implements CommandLineRunner {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("========================================");
        System.out.println("JACKSON CONFIGURATION VERIFICATION");
        System.out.println("========================================");
        
        try {
            // Test if JavaTimeModule is registered
            Instant now = Instant.now();
            String json = objectMapper.writeValueAsString(now);
            
            System.out.println("✅ SUCCESS: JavaTimeModule is properly configured!");
            System.out.println("✅ Instant serialized as: " + json);
            System.out.println("✅ Registered modules: " + objectMapper.getRegisteredModuleIds());
            
        } catch (Exception e) {
            System.err.println("❌ ERROR: JavaTimeModule is NOT properly configured!");
            System.err.println("❌ Error: " + e.getMessage());
            System.err.println("❌ This will cause serialization errors for Instant, LocalDateTime, etc.");
            e.printStackTrace();
        }
        
        System.out.println("========================================");
    }
}