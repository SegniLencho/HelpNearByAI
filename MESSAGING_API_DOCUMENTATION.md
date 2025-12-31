# Messaging API Documentation

## Base Configuration

- **Base URL**: `https://helpnearbyai-production.up.railway.app`
- **WebSocket URL**: `wss://helpnearbyai-production.up.railway.app/ws`
- **API Base Path**: `/api/messages`

---

## Data Models

### MessageDto

```json
{
  "id": "string (UUID)",
  "senderId": "string (User ID)",
  "receiverId": "string (User ID)",
  "content": "string (max 2000 characters)",
  "timestamp": "string (ISO 8601 DateTime format: YYYY-MM-DDTHH:mm:ss)",
  "isRead": "boolean"
}
```

**Example:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "senderId": "user123",
  "receiverId": "user456",
  "content": "Hello! How are you?",
  "timestamp": "2024-01-15T10:30:00",
  "isRead": false
}
```

---

## REST API Endpoints

### 1. Send Message (REST Alternative)

Send a message via REST API. This is an alternative to WebSocket if needed.

**Endpoint:** `POST /api/messages/send`

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "senderId": "string (required)",
  "receiverId": "string (required)",
  "content": "string (required, max 2000 characters)"
}
```

**Request Example:**
```json
{
  "senderId": "user123",
  "receiverId": "user456",
  "content": "Hello! This is a test message."
}
```

**Response:** `200 OK`
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "senderId": "user123",
  "receiverId": "user456",
  "content": "Hello! This is a test message.",
  "timestamp": "2024-01-15T10:30:00",
  "isRead": false
}
```

**cURL Example:**
```bash
curl -X POST https://helpnearbyai-production.up.railway.app/api/messages/send \
  -H "Content-Type: application/json" \
  -d '{
    "senderId": "user123",
    "receiverId": "user456",
    "content": "Hello! This is a test message."
  }'
```

---

### 2. Get Conversation

Retrieve all messages between two users (conversation history).

**Endpoint:** `GET /api/messages/conversation/{userId1}/{userId2}`

**Path Parameters:**
- `userId1` (string, required): First user ID
- `userId2` (string, required): Second user ID

**Response:** `200 OK`
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "senderId": "user123",
    "receiverId": "user456",
    "content": "Hello!",
    "timestamp": "2024-01-15T10:30:00",
    "isRead": true
  },
  {
    "id": "660e8400-e29b-41d4-a716-446655440001",
    "senderId": "user456",
    "receiverId": "user123",
    "content": "Hi there!",
    "timestamp": "2024-01-15T10:31:00",
    "isRead": false
  }
]
```

**cURL Example:**
```bash
curl -X GET https://helpnearbyai-production.up.railway.app/api/messages/conversation/user123/user456
```

---

### 3. Get Unread Messages

Retrieve all unread messages for a specific user.

**Endpoint:** `GET /api/messages/unread/{userId}`

**Path Parameters:**
- `userId` (string, required): User ID to get unread messages for

**Response:** `200 OK`
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "senderId": "user456",
    "receiverId": "user123",
    "content": "Hello!",
    "timestamp": "2024-01-15T10:30:00",
    "isRead": false
  },
  {
    "id": "660e8400-e29b-41d4-a716-446655440001",
    "senderId": "user789",
    "receiverId": "user123",
    "content": "How are you?",
    "timestamp": "2024-01-15T10:32:00",
    "isRead": false
  }
]
```

**cURL Example:**
```bash
curl -X GET https://helpnearbyai-production.up.railway.app/api/messages/unread/user123
```

---

### 4. Get Conversation Partners

Get a list of all users that the current user has conversations with.

**Endpoint:** `GET /api/messages/conversations/{userId}`

**Path Parameters:**
- `userId` (string, required): User ID to get conversation partners for

**Response:** `200 OK`
```json
[
  "user456",
  "user789",
  "user101"
]
```

**cURL Example:**
```bash
curl -X GET https://helpnearbyai-production.up.railway.app/api/messages/conversations/user123
```

---

### 5. Mark Messages as Read

Mark all messages from a specific sender to the receiver as read.

**Endpoint:** `POST /api/messages/mark-read/{senderId}/{receiverId}`

**Path Parameters:**
- `senderId` (string, required): User ID of the message sender
- `receiverId` (string, required): User ID of the message receiver

**Response:** `200 OK` (Empty body)

**cURL Example:**
```bash
curl -X POST https://helpnearbyai-production.up.railway.app/api/messages/mark-read/user456/user123
```

---

## WebSocket Integration

### Connection Setup

The messaging system uses **STOMP over WebSocket** for real-time communication.

**Connection URL:**
- Production: `wss://helpnearbyai-production.up.railway.app/ws`

**Supported Libraries:**
- JavaScript: `stompjs` and `sockjs-client`
- React Native: `@stomp/stompjs` and `react-native-stomp-websocket`
- Android: `com.github.NaikSoftware:StompProtocolAndroid`
- iOS: `Starscream` with STOMP client

---

### JavaScript/TypeScript Example (Web/React)

```javascript
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

class MessagingService {
  constructor() {
    this.client = null;
    this.userId = null;
  }

  connect(userId, onMessageReceived, onError) {
    this.userId = userId;
    
    // Create SockJS connection
    const socket = new SockJS('https://helpnearbyai-production.up.railway.app/ws');
    
    // Create STOMP client
    this.client = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      debug: function(str) {
        console.log(str);
      },
      onConnect: () => {
        console.log('Connected to WebSocket');
        
        // Subscribe to personal message queue
        this.client.subscribe(`/user/${userId}/queue/messages`, (message) => {
          const messageDto = JSON.parse(message.body);
          onMessageReceived(messageDto);
        });
      },
      onStompError: (frame) => {
        console.error('STOMP error:', frame);
        onError(frame);
      },
      onWebSocketClose: () => {
        console.log('WebSocket connection closed');
      }
    });

    // Set userId in headers before connecting
    this.client.connectHeaders = {
      userId: userId
    };

    // Activate the client
    this.client.activate();
  }

  sendMessage(senderId, receiverId, content) {
    if (!this.client || !this.client.connected) {
      console.error('WebSocket not connected');
      return;
    }

    const messageDto = {
      senderId: senderId,
      receiverId: receiverId,
      content: content
    };

    this.client.publish({
      destination: '/app/chat.send',
      body: JSON.stringify(messageDto)
    });
  }

  disconnect() {
    if (this.client) {
      this.client.deactivate();
      this.client = null;
    }
  }
}

// Usage example
const messagingService = new MessagingService();

messagingService.connect(
  'user123',
  (message) => {
    console.log('Received message:', message);
    // Handle incoming message (e.g., update UI)
  },
  (error) => {
    console.error('Connection error:', error);
  }
);

// Send a message
messagingService.sendMessage('user123', 'user456', 'Hello from WebSocket!');

// Disconnect when done
// messagingService.disconnect();
```

---

### React Native Example

```javascript
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

// For React Native, you may need to use a WebSocket polyfill
// Install: npm install react-native-websocket

class MessagingService {
  constructor() {
    this.client = null;
    this.userId = null;
  }

  connect(userId, onMessageReceived) {
    this.userId = userId;
    
    const socket = new SockJS('https://helpnearbyai-production.up.railway.app/ws');
    
    this.client = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        console.log('Connected');
        
        this.client.subscribe(`/user/${userId}/queue/messages`, (message) => {
          const messageDto = JSON.parse(message.body);
          onMessageReceived(messageDto);
        });
      }
    });

    this.client.connectHeaders = { userId: userId };
    this.client.activate();
  }

  sendMessage(senderId, receiverId, content) {
    if (!this.client?.connected) return;

    this.client.publish({
      destination: '/app/chat.send',
      body: JSON.stringify({ senderId, receiverId, content })
    });
  }

  disconnect() {
    this.client?.deactivate();
  }
}

export default MessagingService;
```

---

## WebSocket API Details

### Connecting

1. **Establish WebSocket connection** to `wss://helpnearbyai-production.up.railway.app/ws`
2. **Send CONNECT frame** with `userId` in headers:
   ```
   CONNECT
   userId:user123
   ```
3. **Subscribe** to personal message queue: `/user/{userId}/queue/messages`

### Sending Messages

**STOMP Destination:** `/app/chat.send`

**Message Body (JSON):**
```json
{
  "senderId": "string",
  "receiverId": "string",
  "content": "string"
}
```

### Receiving Messages

**Subscription Destination:** `/user/{userId}/queue/messages`

Messages are delivered to this queue in real-time when:
- A message is sent to the user
- A message is sent by the user (confirmation)

**Received Message Format:**
```json
{
  "id": "string",
  "senderId": "string",
  "receiverId": "string",
  "content": "string",
  "timestamp": "string",
  "isRead": "boolean"
}
```

---

## Integration Flow

### Recommended Implementation Steps

1. **Initialize WebSocket Connection**
   - Connect when user logs in
   - Subscribe to personal message queue
   - Handle connection errors and reconnection

2. **Load Conversation History**
   - Use `GET /api/messages/conversation/{userId1}/{userId2}` to load chat history
   - Display messages in chronological order

3. **Send Messages**
   - Use WebSocket `/app/chat.send` for real-time messages
   - Fallback to REST `POST /api/messages/send` if WebSocket unavailable
   - Update UI optimistically, then sync with server response

4. **Handle Incoming Messages**
   - Listen to `/user/{userId}/queue/messages` subscription
   - Update UI when new message received
   - Show notification for messages when app is in background

5. **Mark Messages as Read**
   - Call `POST /api/messages/mark-read/{senderId}/{receiverId}` when user views conversation
   - Update local message state

6. **Load Conversation List**
   - Use `GET /api/messages/conversations/{userId}` to get list of conversation partners
   - Use `GET /api/messages/unread/{userId}` to show unread counts

---

## Error Handling

### HTTP Status Codes

- `200 OK`: Request successful
- `400 Bad Request`: Invalid request payload or parameters
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server error

### WebSocket Errors

- **Connection Failed**: Implement retry logic with exponential backoff
- **STOMP Error**: Check error frame for details, may need to reconnect
- **Message Send Failed**: Fallback to REST API endpoint

---

## Best Practices

1. **Always connect with userId** in WebSocket headers for proper user identification
2. **Handle reconnection** gracefully when connection drops
3. **Use REST API** for initial conversation load (more reliable)
4. **Use WebSocket** for real-time messaging and updates
5. **Implement message queuing** on client side if sending fails
6. **Mark messages as read** when user views conversation
7. **Handle offline scenarios** by storing messages locally and syncing when online

---

## Testing

### Test WebSocket Connection

```javascript
// Quick test in browser console
const socket = new SockJS('https://helpnearbyai-production.up.railway.app/ws');
const client = new Client({ webSocketFactory: () => socket });
client.connectHeaders = { userId: 'testuser' };
client.onConnect = () => {
  console.log('Connected!');
  client.subscribe('/user/testuser/queue/messages', (msg) => {
    console.log('Received:', JSON.parse(msg.body));
  });
};
client.activate();

// Send test message
setTimeout(() => {
  client.publish({
    destination: '/app/chat.send',
    body: JSON.stringify({
      senderId: 'testuser',
      receiverId: 'testuser2',
      content: 'Test message'
    })
  });
}, 2000);
```

---

## Support

For questions or issues, contact the backend team.

**API Version:** 1.0.0  
**Last Updated:** 2024

