# Firebase Push Notification Service - Test Payloads

## Base URL
```
http://localhost:8080/api
```

---

## 1. Register/Update FCM Token

**Endpoint:** `PUT /api/user/{userId}/fcm-token`

**Purpose:** Register or update a user's FCM token in the database.

### Request Payload:
```json
{
  "fcmToken": "dK3jP9mXqR8sT5vW2yZ6bN1cF4hJ7kL0mP3qR6sT9vW2yZ5bN8cF1hJ4kL7mP0qR3sT6vW9yZ2bN5cF8hJ1kL4mP7qR0"
}
```

### cURL Example:
```bash
curl -X PUT http://localhost:8080/api/user/user123/fcm-token \
  -H "Content-Type: application/json" \
  -d '{
    "fcmToken": "dK3jP9mXqR8sT5vW2yZ6bN1cF4hJ7kL0mP3qR6sT9vW2yZ5bN8cF1hJ4kL7mP0qR3sT6vW9yZ2bN5cF8hJ1kL4mP7qR0"
  }'
```

### Expected Response:
```json
{
  "id": "user123",
  "name": "John Doe",
  "email": "john@example.com",
  "fcmToken": "dK3jP9mXqR8sT5vW2yZ6bN1cF4hJ7kL0mP3qR6sT9vW2yZ5bN8cF1hJ4kL7mP0qR3sT6vW9yZ2bN5cF8hJ1kL4mP7qR0",
  ...
}
```

---

## 2. Send Notification to Single User

**Endpoint:** `POST /api/notifications/user`

**Purpose:** Send a push notification to a single user by their user ID.

### Example 1: Basic Notification
```json
{
  "userId": "user123",
  "title": "New Request",
  "body": "You have a new help request nearby!"
}
```

### Example 2: Notification with Image
```json
{
  "userId": "user123",
  "title": "Request Accepted",
  "body": "Your help request has been accepted by John Doe",
  "imageUrl": "https://example.com/images/request-accepted.png"
}
```

### Example 3: Notification with Additional Data
```json
{
  "userId": "user123",
  "title": "New Message",
  "body": "You have a new message",
  "data": "{\"requestId\":\"req456\",\"type\":\"message\",\"senderId\":\"user789\"}"
}
```

### Example 4: Complete Notification
```json
{
  "userId": "user123",
  "title": "Help Request Update",
  "body": "Your help request status has been updated",
  "imageUrl": "https://example.com/images/status-update.png",
  "data": "{\"requestId\":\"req456\",\"status\":\"IN_PROGRESS\",\"timestamp\":\"2024-01-15T10:30:00Z\"}"
}
```

### cURL Example:
```bash
curl -X POST http://localhost:8080/api/notifications/user \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "title": "New Request",
    "body": "You have a new help request nearby!"
  }'
```

### Expected Response:
```json
{
  "success": true,
  "message": "Notification sent successfully",
  "messageId": "projects/helpnearby-f7498/messages/0:1234567890"
}
```

---

## 3. Send Notification to Multiple Users

**Endpoint:** `POST /api/notifications/users`

**Purpose:** Send a push notification to multiple users simultaneously.

### Example 1: Basic Multi-User Notification
```json
{
  "userIds": ["user123", "user456", "user789"],
  "title": "Important Announcement",
  "body": "All users: New features are now available!"
}
```

### Example 2: Multi-User Notification with Image
```json
{
  "userIds": ["user123", "user456", "user789"],
  "title": "Community Update",
  "body": "Check out the new community guidelines",
  "imageUrl": "https://example.com/images/community-update.jpg"
}
```

### Example 3: Emergency Alert to Multiple Users
```json
{
  "userIds": ["user123", "user456"],
  "title": "Emergency Alert",
  "body": "There is an emergency in your area",
  "data": "{\"type\":\"emergency\",\"location\":{\"lat\":40.7128,\"lng\":-74.0060},\"priority\":\"high\"}"
}
```

### cURL Example:
```bash
curl -X POST http://localhost:8080/api/notifications/users \
  -H "Content-Type: application/json" \
  -d '{
    "userIds": ["user123", "user456", "user789"],
    "title": "Important Announcement",
    "body": "All users: New features are now available!"
  }'
```

### Expected Response:
```json
{
  "success": true,
  "message": "Notifications sent to 3 users. Success: 3, Failed: 0"
}
```

---

## 4. Send Notification to Topic

**Endpoint:** `POST /api/notifications/topic`

**Purpose:** Send a push notification to all users subscribed to a specific topic.

### Example 1: Basic Topic Notification
```json
{
  "topic": "all_users",
  "title": "System Maintenance",
  "body": "Scheduled maintenance will occur tonight at 2 AM"
}
```

### Example 2: Location-Based Topic Notification
```json
{
  "topic": "newyork_users",
  "title": "Weather Alert",
  "body": "Severe weather warning for New York area",
  "imageUrl": "https://example.com/images/weather-alert.png"
}
```

### Example 3: Topic Notification with Data
```json
{
  "topic": "help_requesters",
  "title": "New Feature Available",
  "body": "You can now add multiple images to your requests",
  "data": "{\"feature\":\"multi_image_upload\",\"version\":\"2.0\",\"releaseDate\":\"2024-01-15\"}"
}
```

### Example 4: Emergency Broadcast
```json
{
  "topic": "all_users",
  "title": "Emergency Alert",
  "body": "Please stay indoors due to severe weather conditions",
  "imageUrl": "https://example.com/images/emergency-banner.jpg",
  "data": "{\"alertType\":\"weather\",\"severity\":\"critical\",\"expiresAt\":\"2024-01-15T18:00:00Z\"}"
}
```

### cURL Example:
```bash
curl -X POST http://localhost:8080/api/notifications/topic \
  -H "Content-Type: application/json" \
  -d '{
    "topic": "all_users",
    "title": "System Maintenance",
    "body": "Scheduled maintenance will occur tonight at 2 AM"
  }'
```

### Expected Response:
```json
{
  "success": true,
  "message": "Notification sent successfully to topic",
  "messageId": "projects/helpnearby-f7498/messages/0:1234567890"
}
```

---

## Testing Steps

1. **Register FCM Token**: First, register an FCM token for a user
   ```bash
   PUT /api/user/user123/fcm-token
   ```

2. **Send Test Notification**: Test sending a notification to that user
   ```bash
   POST /api/notifications/user
   ```

3. **Test Multiple Users**: Register tokens for multiple users and test batch notifications
   ```bash
   POST /api/notifications/users
   ```

4. **Test Topics**: Subscribe users to topics and test topic-based notifications
   ```bash
   POST /api/notifications/topic
   ```

---

## Important Notes

- ✅ Replace placeholder values (`user123`, etc.) with actual user IDs from your database
- ✅ Replace FCM token examples with actual tokens from your mobile app
- ✅ All user IDs must exist in your database before sending notifications
- ✅ Topics must follow Firebase naming rules (alphanumeric and `-_~` characters only)
- ✅ The `data` field should contain a JSON string for additional payload data
- ✅ Image URLs should be publicly accessible HTTPS URLs
- ✅ Required fields are validated: `userId`, `userIds`, `topic`, `title`, `body`, `fcmToken`

---

## Error Responses

### User Not Found
```json
{
  "success": false,
  "message": "User does not have a registered FCM token"
}
```

### Invalid Request
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    {
      "field": "userId",
      "message": "User ID is required"
    }
  ]
}
```

---

## Quick Test Collection

### Complete Test Flow:

1. Register token:
```bash
curl -X PUT http://localhost:8080/api/user/test-user-1/fcm-token \
  -H "Content-Type: application/json" \
  -d '{"fcmToken":"YOUR_ACTUAL_FCM_TOKEN_HERE"}'
```

2. Send notification:
```bash
curl -X POST http://localhost:8080/api/notifications/user \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "test-user-1",
    "title": "Test Notification",
    "body": "This is a test notification from HelpNearBy!"
  }'
```

3. Send to multiple users:
```bash
curl -X POST http://localhost:8080/api/notifications/users \
  -H "Content-Type: application/json" \
  -d '{
    "userIds": ["test-user-1", "test-user-2"],
    "title": "Batch Test",
    "body": "Testing batch notifications"
  }'
```

4. Send to topic:
```bash
curl -X POST http://localhost:8080/api/notifications/topic \
  -H "Content-Type: application/json" \
  -d '{
    "topic": "test_topic",
    "title": "Topic Test",
    "body": "Testing topic notifications"
  }'
```
