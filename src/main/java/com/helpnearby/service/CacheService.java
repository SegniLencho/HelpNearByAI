package com.helpnearby.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CacheService {

    @Autowired
    private CacheManager cacheManager;

    /**
     * Clear all caches
     */
    public void clearAllCaches() {
        cacheManager.getCacheNames().forEach(cacheName -> 
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
    }

    /**
     * Clear specific cache by name
     */
    public void clearCache(String cacheName) {
        Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
    }

    /**
     * Clear user-specific caches when user data changes
     */
    public void clearUserCaches(String userId) {
        // Clear user requests cache
        Objects.requireNonNull(cacheManager.getCache("userRequests")).evict(userId);
        
        // Clear user messaging caches
        Objects.requireNonNull(cacheManager.getCache("unreadMessages")).evict(userId);
        Objects.requireNonNull(cacheManager.getCache("conversationPartners")).evict(userId);
        
        // Note: Conversation cache keys are composite, so we clear the entire cache
        // In production, you might want to implement a more sophisticated approach
        Objects.requireNonNull(cacheManager.getCache("conversations")).clear();
    }

    /**
     * Get cache statistics (if available)
     */
    public void printCacheInfo() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            System.out.println("Cache: " + cacheName + " exists: " + 
                (cacheManager.getCache(cacheName) != null));
        });
    }
}