# API Testing Guide

## Quick Test Commands

### 1. Test Server Connectivity
```bash
curl http://localhost:8080/api/debug/ping
```

### 2. Test Java 8 Time Serialization
```bash
curl http://localhost:8080/api/test/instant
```

### 3. Test Requests Endpoint (The Fixed One)
```bash
# Get all requests (default: page 0, size 5)
curl http://localhost:8080/api/requests

# Get specific page
curl "http://localhost:8080/api/requests?page=0&size=10"

# Get with pagination
curl "http://localhost:8080/api/requests?page=1&size=5"
```

### 4. Test Form Metadata
```bash
curl http://localhost:8080/api/requests/form-metadata
```

### 5. Test Redis Cache
```bash
curl http://localhost:8080/api/redis-test/connection
```

### 6. Test Cache Management
```bash
# Get cache info
curl http://localhost:8080/api/cache/info

# Clear all caches
curl -X POST http://localhost:8080/api/cache/clear

# Clear specific cache
curl -X POST http://localhost:8080/api/cache/clear/openRequests
```

## Expected Response Formats

### Requests Endpoint Response
```json
{
  "content": [
    {
      "id": "uuid-here",
      "title": "Need help with moving",
      "description": "Looking for someone to help move furniture",
      "category": "Moving & Delivery",
      "reward": 50.0,
      "urgency": "MEDIUM",
      "latitude": 40.7128,
      "longitude": -74.0060,
      "createdAt": "2026-01-14T10:30:45.123Z",
      "images": "https://s3.amazonaws.com/..."
    }
  ],
  "page": 0,
  "size": 5,
  "totalElements": 25,
  "totalPages": 5,
  "last": false
}
```

### Instant Test Response
```json
{
  "instant": "2026-01-14T10:30:45.123Z",
  "localDateTime": "2026-01-14T10:30:45.123",
  "message": "If you see this, Java 8 time serialization is working!"
}
```

### Form Metadata Response
```json
{
  "fields": {
    "title": {
      "placeholder": "Enter a clear title for your request",
      "type": "text",
      "required": true,
      "maxLength": 255,
      "validation": "required|min:5|max:255",
      "hint": "Be specific about what help you need"
    }
  },
  "categories": ["Home & Garden", "Transportation", ...],
  "urgencyLevels": ["LOW", "MEDIUM", "URGENT"],
  "statusOptions": ["OPEN", "INPROGRESS", "CLOSED"]
}
```

## Testing with Postman

### Import Collection
Create a new Postman collection with these endpoints:

1. **GET** `http://localhost:8080/api/requests`
2. **GET** `http://localhost:8080/api/requests?page=0&size=10`
3. **GET** `http://localhost:8080/api/test/instant`
4. **GET** `http://localhost:8080/api/debug/ping`
5. **GET** `http://localhost:8080/api/requests/form-metadata`

### Environment Variables
```
BASE_URL = http://localhost:8080
```

## Common Issues and Solutions

### Issue: Connection Refused
**Solution**: Make sure the Spring Boot application is running
```bash
mvn spring-boot:run
```

### Issue: 404 Not Found
**Solution**: Check the endpoint URL and ensure the controller is properly mapped

### Issue: 500 Internal Server Error
**Solution**: Check application logs for stack traces
```bash
tail -f logs/application.log
```

### Issue: Dates showing as timestamps
**Solution**: Verify Jackson configuration is loaded
```bash
curl http://localhost:8080/api/test/instant
```

### Issue: Empty response
**Solution**: Check if database has data
```bash
# Check database connection in logs
# Verify data exists in requests table
```

## Performance Testing

### Test Cache Performance
```bash
# First call (no cache) - should be slower
time curl http://localhost:8080/api/requests

# Second call (cached) - should be faster
time curl http://localhost:8080/api/requests
```

### Load Testing with Apache Bench
```bash
# 100 requests, 10 concurrent
ab -n 100 -c 10 http://localhost:8080/api/requests

# With authentication (if needed)
ab -n 100 -c 10 -H "Authorization: Bearer YOUR_TOKEN" http://localhost:8080/api/requests
```

## Debugging Tips

### Enable Debug Logging
Add to `application.properties`:
```properties
logging.level.com.helpnearby=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.com.fasterxml.jackson=DEBUG
```

### Check Jackson Configuration
```bash
# Look for this in startup logs:
# "Registering JavaTimeModule"
# "Jackson ObjectMapper configured"
```

### Verify Redis Connection
```bash
curl http://localhost:8080/api/redis-test/connection
```

### Check Cache Hit/Miss
Enable cache logging:
```properties
logging.level.org.springframework.cache=DEBUG
```

## API Endpoints Summary

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/requests` | GET | Get paginated requests |
| `/api/requests/{id}` | GET | Get single request |
| `/api/requests/user/{userId}` | GET | Get user's requests |
| `/api/requests` | POST | Create new request |
| `/api/requests/{id}` | PUT | Update request |
| `/api/requests/{id}` | DELETE | Delete request |
| `/api/requests/form-metadata` | GET | Get form metadata |
| `/api/test/instant` | GET | Test date serialization |
| `/api/debug/ping` | GET | Test connectivity |
| `/api/cache/clear` | POST | Clear all caches |
| `/api/redis-test/connection` | GET | Test Redis connection |

## Success Criteria

✅ All endpoints return 200 OK
✅ Dates are in ISO-8601 format (not timestamps)
✅ Pagination works correctly
✅ Cache improves response time
✅ No serialization errors in logs