package com.helpnearby.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/redis-test")
public class RedisTestController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/connection")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Test basic connection
            redisTemplate.opsForValue().set("test:connection", "Redis is working!");
            String value = (String) redisTemplate.opsForValue().get("test:connection");
            
            // Test Java 8 time serialization
            Instant now = Instant.now();
            redisTemplate.opsForValue().set("test:instant", now);
            Instant retrievedInstant = (Instant) redisTemplate.opsForValue().get("test:instant");
            
            response.put("status", "success");
            response.put("message", "Redis connection successful");
            response.put("testValue", value);
            response.put("instantTest", "Original: " + now + ", Retrieved: " + retrievedInstant);
            response.put("instantMatch", now.equals(retrievedInstant));
            
            // Clean up test keys
            redisTemplate.delete("test:connection");
            redisTemplate.delete("test:instant");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Redis connection failed: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/set/{key}")
    public ResponseEntity<String> setValue(@PathVariable String key, @RequestBody String value) {
        try {
            redisTemplate.opsForValue().set("test:" + key, value);
            return ResponseEntity.ok("Value set successfully for key: " + key);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error setting value: " + e.getMessage());
        }
    }

    @GetMapping("/get/{key}")
    public ResponseEntity<Object> getValue(@PathVariable String key) {
        try {
            Object value = redisTemplate.opsForValue().get("test:" + key);
            if (value != null) {
                return ResponseEntity.ok(value);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error getting value: " + e.getMessage());
        }
    }
}