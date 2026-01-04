package com.helpnearby.controller;

import com.helpnearby.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cache")
public class CacheController {

    @Autowired
    private CacheService cacheService;

    @PostMapping("/clear")
    public ResponseEntity<String> clearAllCaches() {
        cacheService.clearAllCaches();
        return ResponseEntity.ok("All caches cleared successfully");
    }

    @PostMapping("/clear/{cacheName}")
    public ResponseEntity<String> clearSpecificCache(@PathVariable String cacheName) {
        cacheService.clearCache(cacheName);
        return ResponseEntity.ok("Cache '" + cacheName + "' cleared successfully");
    }

    @PostMapping("/clear/user/{userId}")
    public ResponseEntity<String> clearUserCaches(@PathVariable String userId) {
        cacheService.clearUserCaches(userId);
        return ResponseEntity.ok("User caches for '" + userId + "' cleared successfully");
    }

    @GetMapping("/info")
    public ResponseEntity<String> getCacheInfo() {
        cacheService.printCacheInfo();
        return ResponseEntity.ok("Cache info printed to console");
    }
}