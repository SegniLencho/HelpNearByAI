# Redis Caching Implementation

## Overview
This implementation adds Redis caching to the HelpNearBy application, replacing the previous EhCache setup with a distributed Redis cache for better performance and scalability.

## Configuration

### Redis Connection
- **URL**: `rediss://default:AVH9AAIncDI5YmFkMWViMmRkZjg0NzgwYTUxZTYzODgyNmRjNTliOXAyMjA5ODk@devoted-hyena-20989.upstash.io:6379`
- **SSL**: Enabled (rediss://)
- **Connection Pool**: Max 8 active, 8 max idle, 0 min idle
- **Timeout**: 2000ms

### Cache Configuration
Different caches have optimized TTL values:

| Cache Name | TTL | Purpose |
|------------|-----|---------|
| `requests` | 15 minutes | Individual request details |
| `userRequests` | 15 minutes | User's request lists |
| `openRequests` | 10 minutes | Public request feed |
| `conversations` | 30 minutes | Message history |
| `unreadMessages` | 5 minutes | Unread message counts |
| `conversationPartners` | 1 hour | User conversation lists |

## Cached Operations

### RequestService
- ✅ `getAllRequests()` - Cached by page/size
- ✅ `getRequestById()` - Cached by request ID
- ✅ `getRequestsByUserId()` - Cached by user ID
- ✅ `getRequestsByUserIdAndStatus()` - Cached by user ID + status
- ✅ Cache invalidation on create/update/delete operations

### MessageService
- ✅ `getConversationBetweenUsers()` - Cached by user pair
- ✅ `getUnreadMessages()` - Cached by user ID
- ✅ `getUserConversationPartners()` - Cached by user ID
- ✅ Cache invalidation on message creation and read status updates

## Cache Keys Strategy

### Request Caches
```
requests:{requestId}
userRequests:{userId}
userRequests:{userId}:status:{status}
openRequests:content:page:{page}:size:{size}  # List content only
openRequests:count                            # Total count for pagination
```

### Message Caches
```
conversations:{userId1}:{userId2}
unreadMessages:{userId}
conversationPartners:{userId}
```

## Caching Strategy for Complex Objects

### Page Object Handling
Spring Data `Page` objects are complex and don't serialize well to Redis. The solution:

1. **Cache Content Separately**: Cache `List<RequestListDTO>` instead of `Page<RequestListDTO>`
2. **Cache Count Separately**: Cache total count for pagination metadata
3. **Reconstruct Page**: Combine cached content + count to rebuild `Page` object

This approach:
- ✅ Avoids serialization issues with complex Spring Data types
- ✅ Provides better cache granularity
- ✅ Maintains full pagination functionality
- ✅ Reduces cache invalidation complexity

## Cache Management

### Manual Cache Control
Use the `/api/cache` endpoints for cache management:

```bash
# Clear all caches
POST /api/cache/clear

# Clear specific cache
POST /api/cache/clear/{cacheName}

# Clear user-specific caches
POST /api/cache/clear/user/{userId}

# Get cache information
GET /api/cache/info
```

### Automatic Cache Invalidation
Caches are automatically invalidated when:

- **Request Operations**:
  - Create: Clears `openRequests` and user's `userRequests`
  - Update: Clears specific request, open requests, and user requests
  - Delete: Clears all related caches

- **Message Operations**:
  - Send message: Clears conversations, unread messages, and partner lists
  - Mark as read: Clears unread messages and conversations

## Performance Benefits

### Expected Improvements
- **Database Query Reduction**: 70-90% for cached operations
- **Response Time**: 50-150ms → 5-15ms (10x faster)
- **Scalability**: Distributed cache across multiple instances
- **Persistence**: Cache survives server restarts

### Monitoring
Monitor cache performance through:
- Application logs
- Redis monitoring tools
- Custom metrics (can be added)

## Dependencies Added

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
</dependency>
```

## Configuration Files Modified

1. **pom.xml** - Added Redis and Jackson JSR310 dependencies
2. **application.properties** - Added Redis connection, cache, and Jackson configuration
3. **RedisConfig.java** - New Redis configuration class with Java 8 time support
4. **RequestService.java** - Added caching annotations
5. **MessageService.java** - Added caching annotations
6. **CacheService.java** - New cache management service
7. **CacheController.java** - New cache management endpoints
8. **RedisTestController.java** - New Redis connection testing endpoints

## Testing

### Verify Redis Connection
1. Start the application
2. Test Redis connection: `GET /api/redis-test/connection`
3. Check logs for Redis connection success
4. Test any cached endpoint twice - second call should be faster

### Test Java 8 Time Serialization
The Redis test endpoint will verify that `Instant` and other Java 8 time types are properly serialized/deserialized.

### Test Cache Invalidation
1. Create a request via API
2. Verify open requests cache is cleared
3. Update a request
4. Verify specific request cache is updated

### Performance Testing
```bash
# Test cached vs non-cached performance
curl -w "@curl-format.txt" -s -o /dev/null http://localhost:8080/api/requests
```

## Troubleshooting

### Common Issues
1. **Connection Failed**: Check Redis URL and credentials
2. **Serialization Errors**: Ensure Jackson JSR310 module is properly configured
3. **Cache Not Working**: Verify `@EnableCaching` is present
4. **Memory Issues**: Monitor Redis memory usage
5. **Java 8 Time Issues**: Check ObjectMapper configuration includes JavaTimeModule
6. **Page Serialization Issues**: Use separate content and count caching instead of caching Page objects directly

### Debug Mode
Enable debug logging in `application.properties`:
```properties
logging.level.org.springframework.cache=DEBUG
logging.level.org.springframework.data.redis=DEBUG
logging.level.com.fasterxml.jackson=DEBUG
```

## Future Enhancements

1. **Cache Warming**: Pre-populate frequently accessed data
2. **Cache Metrics**: Add monitoring and alerting
3. **Advanced Invalidation**: More granular cache key patterns
4. **Compression**: Enable Redis compression for large objects
5. **Clustering**: Redis cluster support for high availability