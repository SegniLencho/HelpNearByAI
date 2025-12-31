# Messaging API - Quick Reference Guide

## Base URLs
- **REST API**: `https://helpnearbyai-production.up.railway.app/api/messages`
- **WebSocket**: `wss://helpnearbyai-production.up.railway.app/ws`

---

## REST Endpoints Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/messages/send` | Send a message |
| `GET` | `/api/messages/conversation/{userId1}/{userId2}` | Get conversation history |
| `GET` | `/api/messages/unread/{userId}` | Get unread messages |
| `GET` | `/api/messages/conversations/{userId}` | Get conversation partners list |
| `POST` | `/api/messages/mark-read/{senderId}/{receiverId}` | Mark messages as read |

---

## Request/Response Formats

### Send Message (POST /api/messages/send)

**Request:**
```json
{
  "senderId": "user123",
  "receiverId": "user456",
  "content": "Hello!"
}
```

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "senderId": "user123",
  "receiverId": "user456",
  "content": "Hello!",
  "timestamp": "2024-01-15T10:30:00",
  "isRead": false
}
```

---

### Get Conversation (GET /api/messages/conversation/{userId1}/{userId2})

**Response:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "senderId": "user123",
    "receiverId": "user456",
    "content": "Hello!",
    "timestamp": "2024-01-15T10:30:00",
    "isRead": true
  }
]
```

---

### Get Unread Messages (GET /api/messages/unread/{userId})

**Response:** Same format as conversation array (MessageDto[])

---

### Get Conversation Partners (GET /api/messages/conversations/{userId})

**Response:**
```json
["user456", "user789", "user101"]
```

---

### Mark as Read (POST /api/messages/mark-read/{senderId}/{receiverId})

**Response:** `200 OK` (empty body)

---

## WebSocket Quick Start

### 1. Connect
```javascript
const socket = new SockJS('https://helpnearbyai-production.up.railway.app/ws');
const client = new Client({ webSocketFactory: () => socket });

client.connectHeaders = { userId: 'user123' };

client.onConnect = () => {
  // Subscribe to receive messages
  client.subscribe('/user/user123/queue/messages', (message) => {
    const msg = JSON.parse(message.body);
    console.log('New message:', msg);
  });
};

client.activate();
```

### 2. Send Message
```javascript
client.publish({
  destination: '/app/chat.send',
  body: JSON.stringify({
    senderId: 'user123',
    receiverId: 'user456',
    content: 'Hello!'
  })
});
```

### 3. Disconnect
```javascript
client.deactivate();
```

---

## MessageDto Fields

| Field | Type | Description |
|-------|------|-------------|
| `id` | string (UUID) | Message unique identifier |
| `senderId` | string | Sender's user ID |
| `receiverId` | string | Receiver's user ID |
| `content` | string | Message content (max 2000 chars) |
| `timestamp` | string (ISO 8601) | Message timestamp |
| `isRead` | boolean | Read status |

---

## Common Integration Patterns

### Load Chat Screen
1. Connect WebSocket with current user ID
2. Subscribe to `/user/{userId}/queue/messages`
3. Call `GET /api/messages/conversation/{currentUserId}/{otherUserId}` to load history
4. Display messages

### Send Message
1. Send via WebSocket: `/app/chat.send`
2. Or fallback to REST: `POST /api/messages/send`
3. Update UI with sent message

### Receive Message
1. Listen to subscription: `/user/{userId}/queue/messages`
2. Parse JSON message
3. Update UI with new message

### Mark Read
1. When user opens conversation, call: `POST /api/messages/mark-read/{senderId}/{currentUserId}`
2. Update local message state

---

## Required Libraries

**JavaScript/React:**
```bash
npm install @stomp/stompjs sockjs-client
```

**React Native:**
```bash
npm install @stomp/stompjs sockjs-client react-native-websocket
```

---

## Error Codes

- `200`: Success
- `400`: Bad Request (invalid payload)
- `404`: Not Found
- `500`: Server Error

