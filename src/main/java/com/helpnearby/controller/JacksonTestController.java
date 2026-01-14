package com.helpnearby.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Test controller to verify Jackson Java 8 time serialization works correctly
 */
@RestController
@RequestMapping("/api/test")
public class JacksonTestController {

    @GetMapping("/instant")
    public ResponseEntity<Map<String, Object>> testInstant() {
        Map<String, Object> response = new HashMap<>();
        response.put("instant", Instant.now());
        response.put("localDateTime", LocalDateTime.now());
        response.put("message", "If you see this, Java 8 time serialization is working!");
        return ResponseEntity.ok(response);
    }
}