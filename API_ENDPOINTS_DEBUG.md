# API Endpoints Debug Guide

## Available Endpoints

### Requests API
Base URL: `http://localhost:8080/api/requests`

1. **GET** `/api/requests` - Get all requests (paginated)
   - Query params: `page` (default: 0), `size` (default: 5)
   - Example: `GET http://localhost:8080/api/requests?page=0&size=5`

2. **GET** `/api/requests/{id}` - Get request by ID
   - Example: `GET http://localhost:8080/api/requests/93f7b4cb-1388-4e6e-b286-b8d9ef3d4917`

3. **POST** `/api/requests` - Create new request
   - Example: `POST http://localhost:8080/api/requests`

4. **PUT** `/api/requests/{id}` - Update request
   - Example: `PUT http://localhost:8080/api/requests/93f7b4cb-1388-4e6e-b286-b8d9ef3d4917`

5. **DELETE** `/api/requests/{id}` - Delete request
   - Example: `DELETE http://localhost:8080/api/requests/93f7b4cb-1388-4e6e-b286-b8d9ef3d4917`

6. **GET** `/api/requests/user/{userId}` - Get requests by user
   - Example: `GET http://localhost:8080/api/requests/user/648h4LMk6rPUgtvZ3i9jzKLQRxn2`

7. **POST** `/api/requests/presign` - Get presigned URL for S3 upload
   - Example: `POST http://localhost:8080/api/requests/presign`

## Troubleshooting 404 Errors

### Step 1: Verify Server is Running
```bash
# Check if server is running on port 8080
curl http://localhost:8080/api/requests
```

### Step 2: Check Application Logs
Look for:
- "Started HelpNearByApplication" - confirms app started
- "Mapped" - shows registered endpoints
- Any error messages during startup

### Step 3: Test Basic Endpoint
```bash
# Test GET endpoint first
curl -X GET http://localhost:8080/api/requests?page=0&size=5
```

### Step 4: Verify Request Exists
```bash
# Check if the request ID exists
curl -X GET http://localhost:8080/api/requests/93f7b4cb-1388-4e6e-b286-b8d9ef3d4917
```

### Step 5: Check URL Format
- Make sure there are no extra slashes
- Verify the ID is correct (no spaces, proper UUID format)
- Check if using HTTP vs HTTPS

## Common Issues

1. **Server not running**: Start the Spring Boot application
2. **Wrong port**: Default is 8080, check application.properties
3. **Context path**: No context path configured, so base is `/`
4. **CORS issues**: Check CorsConfig.java
5. **Request doesn't exist**: The ID might not exist in database

## Testing Commands

```bash
# Test server is up
curl http://localhost:8080/api/requests

# Test GET by ID
curl -X GET http://localhost:8080/api/requests/93f7b4cb-1388-4e6e-b286-b8d9ef3d4917

# Test PUT (update)
curl -X PUT http://localhost:8080/api/requests/93f7b4cb-1388-4e6e-b286-b8d9ef3d4917 \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "648h4LMk6rPUgtvZ3i9jzKLQRxn2",
    "title": "Coffee maker for sale updated",
    "description": "Need to sale this Coffee maker",
    "category": "Coffee maker",
    "reward": 0,
    "latitude": 40.61,
    "longitude": -112.3,
    "urgency": "LOW",
    "status": "OPEN",
    "images": []
  }'
```
