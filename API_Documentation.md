# 🚀 Spring Boot Blog API Documentation

## 📋 Complete API Reference Guide

**Application**: Spring Boot Blog with Real-time Chat & Notifications  
**Version**: 1.0.0  
**Base URL**: `http://localhost:8080`  
**Created**: July 17, 2025  

---

## 🎯 Table of Contents

1. [Authentication Endpoints](#authentication-endpoints)
2. [Blog Management Endpoints](#blog-management-endpoints)
3. [Public Blog Endpoints](#public-blog-endpoints)
4. [Chat Room Endpoints](#chat-room-endpoints)
5. [Test Endpoints](#test-endpoints)
6. [WebSocket Endpoints](#websocket-endpoints)
7. [WebSocket Subscription Topics](#websocket-subscription-topics)
8. [Security Configuration](#security-configuration)
9. [Data Models](#data-models)
10. [Error Handling](#error-handling)

---

## 🔐 Authentication Endpoints

### 1. User Registration
- **Method**: `POST`
- **URL**: `/api/auth/register`
- **Authentication**: Not required
- **Description**: Register a new user account

**Request Body**:
```json
{
  "username": "john_doe",
  "password": "securepassword123"
}
```

**Response (Success - 200)**:
```json
"User registered successfully"
```

**Response (Error - 400)**:
```json
"Error: Username already exists"
```

**Response (Error - 500)**:
```json
"Server error: Internal server error message"
```

---

### 2. User Login
- **Method**: `POST`
- **URL**: `/api/auth/login`
- **Authentication**: Not required
- **Description**: Authenticate user and receive JWT token

**Request Body**:
```json
{
  "username": "john_doe",
  "password": "securepassword123"
}
```

**Response (Success - 200)**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huX2RvZSIsImlhdCI6MTY0MjY4MDAwMCwiZXhwIjoxNjQyNzY2NDAwfQ.signature",
  "type": "Bearer",
  "username": "john_doe",
  "expiresIn": 86400000
}
```

**Response (Error - 401)**:
```json
{
  "token": null,
  "type": "Bearer",
  "username": null,
  "expiresIn": 0
}
```

---

## 📝 Blog Management Endpoints

### 3. Create Blog (Protected)
- **Method**: `POST`
- **URL**: `/api/blogs`
- **Authentication**: JWT Bearer token required
- **Description**: Create a new blog post

**Headers**:
```
Authorization: Bearer <jwt-token>
Content-Type: application/json
```

**Request Body**:
```json
{
  "title": "My First Blog Post",
  "content": "This is the content of my first blog post. It can contain HTML, markdown, or plain text..."
}
```

**Response (Success - 200)**:
```json
{
  "id": 1,
  "title": "My First Blog Post",
  "content": "This is the content of my first blog post. It can contain HTML, markdown, or plain text...",
  "user": {
    "id": 1,
    "username": "john_doe"
  }
}
```

**Real-time Notification**: Triggers WebSocket notification to `/topic/system-notifications`

---

### 4. Update Blog (Protected)
- **Method**: `PUT`
- **URL**: `/api/blogs/{id}`
- **Authentication**: JWT Bearer token required
- **Description**: Update an existing blog post (only by owner)

**Headers**:
```
Authorization: Bearer <jwt-token>
Content-Type: application/json
```

**Path Parameters**:
- `id` (Long): Blog post ID

**Request Body**:
```json
{
  "title": "Updated Blog Title",
  "content": "Updated blog content with new information..."
}
```

**Response (Success - 200)**:
```json
{
  "id": 1,
  "title": "Updated Blog Title",
  "content": "Updated blog content with new information...",
  "user": {
    "id": 1,
    "username": "john_doe"
  }
}
```

**Real-time Notification**: Triggers WebSocket notification to `/topic/system-notifications`

---

### 5. Delete Blog (Protected)
- **Method**: `DELETE`
- **URL**: `/api/blogs/{id}`
- **Authentication**: JWT Bearer token required
- **Description**: Delete a blog post (only by owner)

**Headers**:
```
Authorization: Bearer <jwt-token>
```

**Path Parameters**:
- `id` (Long): Blog post ID

**Response (Success - 200)**:
```json
"Blog deleted"
```

**Real-time Notification**: Triggers WebSocket notification to `/topic/system-notifications`

---

### 6. Get My Blogs (Protected)
- **Method**: `GET`
- **URL**: `/api/blogs/my-blogs`
- **Authentication**: JWT Bearer token required
- **Description**: Get all blog posts created by the authenticated user

**Headers**:
```
Authorization: Bearer <jwt-token>
```

**Response (Success - 200)**:
```json
[
  {
    "id": 1,
    "title": "My First Blog Post",
    "content": "This is the content...",
    "user": {
      "id": 1,
      "username": "john_doe"
    }
  },
  {
    "id": 2,
    "title": "My Second Blog Post",
    "content": "Another blog post...",
    "user": {
      "id": 1,
      "username": "john_doe"
    }
  }
]
```

---

## 🌐 Public Blog Endpoints

### 7. Get All Blogs (Public)
- **Method**: `GET`
- **URL**: `/api/blogs/public`
- **Authentication**: Not required
- **Description**: Get all blog posts from all users

**Response (Success - 200)**:
```json
[
  {
    "id": 1,
    "title": "Public Blog Post",
    "content": "This is a public blog post...",
    "user": {
      "id": 1,
      "username": "john_doe"
    }
  },
  {
    "id": 2,
    "title": "Another Public Post",
    "content": "Another public blog post...",
    "user": {
      "id": 2,
      "username": "jane_doe"
    }
  }
]
```

---

### 8. Get Blog by ID (Public)
- **Method**: `GET`
- **URL**: `/api/blogs/public/{id}`
- **Authentication**: Not required
- **Description**: Get a specific blog post by ID

**Path Parameters**:
- `id` (Long): Blog post ID

**Response (Success - 200)**:
```json
{
  "id": 1,
  "title": "Public Blog Post",
  "content": "This is a public blog post...",
  "user": {
    "id": 1,
    "username": "john_doe"
  }
}
```

---

### 9. Get Blogs by Username (Public)
- **Method**: `GET`
- **URL**: `/api/blogs/public/user/{username}`
- **Authentication**: Not required
- **Description**: Get all blog posts by a specific user

**Path Parameters**:
- `username` (String): Username of the blog author

**Response (Success - 200)**:
```json
[
  {
    "id": 1,
    "title": "John's Blog Post",
    "content": "This is John's blog post...",
    "user": {
      "id": 1,
      "username": "john_doe"
    }
  }
]
```

---

## 💬 Chat Room Endpoints

### 10. Get All Chat Rooms
- **Method**: `GET`
- **URL**: `/api/chat/rooms`
- **Authentication**: Not required
- **Description**: Get all available chat rooms

**Response (Success - 200)**:
```json
{
  "general": {
    "roomId": "general",
    "roomName": "General Discussion",
    "description": "General chat room",
    "createdBy": "admin",
    "createdAt": "2025-01-15T10:30:00",
    "private": false,
    "memberCount": 5
  },
  "tech": {
    "roomId": "tech",
    "roomName": "Tech Talk",
    "description": "Technology discussions",
    "createdBy": "john_doe",
    "createdAt": "2025-01-15T11:00:00",
    "private": false,
    "memberCount": 3
  }
}
```

---

### 11. Get Specific Chat Room
- **Method**: `GET`
- **URL**: `/api/chat/rooms/{roomId}`
- **Authentication**: Not required
- **Description**: Get details of a specific chat room

**Path Parameters**:
- `roomId` (String): Room identifier

**Response (Success - 200)**:
```json
{
  "roomId": "general",
  "roomName": "General Discussion",
  "description": "General chat room",
  "createdBy": "admin",
  "createdAt": "2025-01-15T10:30:00",
  "private": false,
  "memberCount": 5
}
```

---

### 12. Get Room Members
- **Method**: `GET`
- **URL**: `/api/chat/rooms/{roomId}/members`
- **Authentication**: Not required
- **Description**: Get list of members in a specific chat room

**Path Parameters**:
- `roomId` (String): Room identifier

**Response (Success - 200)**:
```json
["john_doe", "jane_doe", "admin", "user1", "user2"]
```

---

### 13. Create Chat Room (Protected)
- **Method**: `POST`
- **URL**: `/api/chat/rooms`
- **Authentication**: JWT Bearer token required
- **Description**: Create a new chat room

**Headers**:
```
Authorization: Bearer <jwt-token>
Content-Type: application/json
```

**Request Body**:
```json
{
  "roomId": "new-room",
  "roomName": "New Discussion Room",
  "description": "A new room for discussions",
  "private": false
}
```

**Response (Success - 200)**:
```json
{
  "roomId": "new-room",
  "roomName": "New Discussion Room",
  "description": "A new room for discussions",
  "createdBy": "john_doe",
  "createdAt": "2025-01-15T12:00:00",
  "private": false,
  "memberCount": 1
}
```

---

### 14. Join Chat Room (Protected)
- **Method**: `POST`
- **URL**: `/api/chat/rooms/{roomId}/join`
- **Authentication**: JWT Bearer token required
- **Description**: Join a specific chat room

**Headers**:
```
Authorization: Bearer <jwt-token>
```

**Path Parameters**:
- `roomId` (String): Room identifier

**Response (Success - 200)**:
```json
"Joined room successfully"
```

---

### 15. Leave Chat Room (Protected)
- **Method**: `POST`
- **URL**: `/api/chat/rooms/{roomId}/leave`
- **Authentication**: JWT Bearer token required
- **Description**: Leave a specific chat room

**Headers**:
```
Authorization: Bearer <jwt-token>
```

**Path Parameters**:
- `roomId` (String): Room identifier

**Response (Success - 200)**:
```json
"Left room successfully"
```

---

## 🧪 Test Endpoints

### 16. Application Health Check
- **Method**: `GET`
- **URL**: `/test`
- **Authentication**: Not required
- **Description**: Check if the application is running

**Response (Success - 200)**:
```json
"Application is running successfully!"
```

---

### 17. WebSocket Status Check
- **Method**: `GET`
- **URL**: `/test-websocket`
- **Authentication**: Not required
- **Description**: Check WebSocket configuration status

**Response (Success - 200)**:
```json
"WebSocket configuration is active. Demo page should be accessible at /websocket-demo.html"
```

---

## 🔌 WebSocket Endpoints

### WebSocket Connection
- **URL**: `ws://localhost:8080/ws`
- **Protocol**: STOMP over WebSocket
- **Authentication**: Connect first, then authenticate via session

### 18. Send Public Message
- **Destination**: `/app/chat.sendMessage`
- **Description**: Send a message to the public chat room
- **Broadcast to**: `/topic/public`

**Request**:
```json
{
  "content": "Hello everyone!",
  "sender": "john_doe",
  "type": "CHAT"
}
```

**Response (Broadcast)**:
```json
{
  "content": "Hello everyone!",
  "sender": "john_doe",
  "type": "CHAT",
  "timestamp": "2025-01-15T12:30:45",
  "room": null
}
```

---

### 19. Join Public Chat
- **Destination**: `/app/chat.addUser`
- **Description**: Join the public chat room
- **Broadcast to**: `/topic/public` and `/topic/user-presence`

**Request**:
```json
{
  "sender": "john_doe",
  "type": "JOIN"
}
```

**Response (Broadcast to /topic/public)**:
```json
{
  "content": "john_doe joined the chat!",
  "sender": "john_doe",
  "type": "JOIN",
  "timestamp": "2025-01-15T12:30:45",
  "room": null
}
```

**Response (Broadcast to /topic/user-presence)**:
```json
{
  "username": "john_doe",
  "online": true,
  "lastSeen": "2025-01-15T12:30:45",
  "status": "online"
}
```

---

### 20. Send Room Message
- **Destination**: `/app/room/{roomId}/sendMessage`
- **Description**: Send a message to a specific room
- **Broadcast to**: `/topic/room/{roomId}`

**Request**:
```json
{
  "content": "Hello room members!",
  "sender": "john_doe",
  "type": "CHAT"
}
```

**Response (Broadcast)**:
```json
{
  "content": "Hello room members!",
  "sender": "john_doe",
  "type": "CHAT",
  "timestamp": "2025-01-15T12:30:45",
  "room": "general"
}
```

---

### 21. Join Room
- **Destination**: `/app/room/{roomId}/join`
- **Description**: Join a specific chat room
- **Broadcast to**: `/topic/room/{roomId}` and `/topic/room-updates`

**Request**:
```json
{
  "sender": "john_doe",
  "type": "JOIN"
}
```

**Response (Broadcast to room)**:
```json
{
  "content": "john_doe joined general",
  "sender": "john_doe",
  "type": "JOIN",
  "timestamp": "2025-01-15T12:30:45",
  "room": "general"
}
```

---

### 22. Leave Room
- **Destination**: `/app/room/{roomId}/leave`
- **Description**: Leave a specific chat room
- **Broadcast to**: `/topic/room/{roomId}` and `/topic/room-updates`

**Request**:
```json
{
  "sender": "john_doe",
  "type": "LEAVE"
}
```

**Response (Broadcast)**:
```json
{
  "content": "john_doe left general",
  "sender": "john_doe",
  "type": "LEAVE",
  "timestamp": "2025-01-15T12:30:45",
  "room": "general"
}
```

---

### 23. Create Room via WebSocket
- **Destination**: `/app/room/create`
- **Description**: Create a new chat room via WebSocket
- **Broadcast to**: `/topic/new-rooms`

**Request**:
```json
{
  "roomId": "new-room",
  "roomName": "New Room",
  "description": "A new chat room",
  "private": false
}
```

**Response (Broadcast)**:
```json
{
  "roomId": "new-room",
  "roomName": "New Room",
  "description": "A new chat room",
  "createdBy": "john_doe",
  "createdAt": "2025-01-15T12:30:45",
  "private": false,
  "memberCount": 1
}
```

---

### 24. List Rooms
- **Destination**: `/app/room/list`
- **Description**: Request list of all available rooms
- **Broadcast to**: `/topic/room-list`

**Request**: No payload required

**Response (Broadcast)**:
```json
{
  "general": {
    "roomId": "general",
    "roomName": "General Discussion",
    "description": "General chat room",
    "createdBy": "admin",
    "createdAt": "2025-01-15T10:30:00",
    "private": false,
    "memberCount": 5
  }
}
```

---

### 25. Typing Indicator
- **Destination**: `/app/chat.typing`
- **Description**: Indicate user is typing
- **Broadcast to**: `/topic/typing`

**Request**:
```json
{
  "sender": "john_doe",
  "type": "TYPING"
}
```

**Response (Broadcast)**:
```json
{
  "sender": "john_doe",
  "type": "TYPING",
  "timestamp": "2025-01-15T12:30:45",
  "content": null,
  "room": null
}
```

---

### 26. Private Message
- **Destination**: `/app/chat.private`
- **Description**: Send a private message to a specific user
- **Send to**: `/user/{recipient}/queue/private`

**Request**:
```json
{
  "content": "Private message",
  "sender": "john_doe",
  "room": "jane_doe",
  "type": "CHAT"
}
```

**Response (Direct to recipient)**:
```json
{
  "content": "Private message",
  "sender": "john_doe",
  "type": "CHAT",
  "timestamp": "2025-01-15T12:30:45",
  "room": "jane_doe"
}
```

---

### 27. Admin System Notification
- **Destination**: `/app/admin.notification`
- **Description**: Send system-wide notifications (admin only)
- **Broadcast to**: `/topic/system-notifications`

**Request**:
```json
{
  "message": "System maintenance in 5 minutes",
  "type": "SYSTEM",
  "sender": "admin"
}
```

**Response (Broadcast)**:
```json
{
  "message": "System maintenance in 5 minutes",
  "type": "SYSTEM",
  "sender": "admin",
  "timestamp": "2025-01-15T12:30:45",
  "data": null
}
```

---

## 📡 WebSocket Subscription Topics

### 28. Subscribe to Public Chat
- **Topic**: `/topic/public`
- **Description**: Receive public chat messages and join/leave notifications

**Message Format**:
```json
{
  "content": "Hello everyone!",
  "sender": "john_doe",
  "type": "CHAT",
  "timestamp": "2025-01-15T12:30:45",
  "room": null
}
```

---

### 29. Subscribe to User Presence
- **Topic**: `/topic/user-presence`
- **Description**: Receive user online/offline status updates

**Message Format**:
```json
{
  "username": "john_doe",
  "online": true,
  "lastSeen": "2025-01-15T12:30:45",
  "status": "online"
}
```

---

### 30. Subscribe to Room Messages
- **Topic**: `/topic/room/{roomId}`
- **Description**: Receive messages from a specific room

**Message Format**:
```json
{
  "content": "Room message",
  "sender": "john_doe",
  "type": "CHAT",
  "timestamp": "2025-01-15T12:30:45",
  "room": "general"
}
```

---

### 31. Subscribe to Room Updates
- **Topic**: `/topic/room-updates`
- **Description**: Receive room member count and info updates

**Message Format**:
```json
{
  "roomId": "general",
  "roomName": "General Discussion",
  "description": "General chat room",
  "createdBy": "admin",
  "createdAt": "2025-01-15T10:30:00",
  "private": false,
  "memberCount": 6
}
```

---

### 32. Subscribe to New Rooms
- **Topic**: `/topic/new-rooms`
- **Description**: Receive notifications about newly created rooms

**Message Format**:
```json
{
  "roomId": "new-room",
  "roomName": "New Room",
  "description": "A new chat room",
  "createdBy": "john_doe",
  "createdAt": "2025-01-15T12:30:45",
  "private": false,
  "memberCount": 1
}
```

---

### 33. Subscribe to Room List
- **Topic**: `/topic/room-list`
- **Description**: Receive complete list of available rooms

**Message Format**:
```json
{
  "general": {
    "roomId": "general",
    "roomName": "General Discussion",
    "description": "General chat room",
    "createdBy": "admin",
    "createdAt": "2025-01-15T10:30:00",
    "private": false,
    "memberCount": 5
  },
  "tech": {
    "roomId": "tech",
    "roomName": "Tech Talk",
    "description": "Technology discussions",
    "createdBy": "john_doe",
    "createdAt": "2025-01-15T11:00:00",
    "private": false,
    "memberCount": 3
  }
}
```

---

### 34. Subscribe to Typing Indicators
- **Topic**: `/topic/typing`
- **Description**: Receive typing status from users

**Message Format**:
```json
{
  "sender": "john_doe",
  "type": "TYPING",
  "timestamp": "2025-01-15T12:30:45",
  "content": null,
  "room": null
}
```

---

### 35. Subscribe to Private Messages
- **Topic**: `/user/{username}/queue/private`
- **Description**: Receive private messages sent to specific user

**Message Format**:
```json
{
  "content": "Private message",
  "sender": "john_doe",
  "type": "CHAT",
  "timestamp": "2025-01-15T12:30:45",
  "room": "jane_doe"
}
```

---

### 36. Subscribe to System Notifications
- **Topic**: `/topic/system-notifications`
- **Description**: Receive system-wide announcements and blog notifications

**Message Format**:
```json
{
  "message": "New blog post created: 'My First Blog Post'",
  "type": "BLOG_CREATED",
  "sender": "john_doe",
  "timestamp": "2025-01-15T12:30:45",
  "data": {
    "blogId": 1,
    "title": "My First Blog Post",
    "author": "john_doe"
  }
}
```

---

## 🛡️ Security Configuration

### JWT Authentication
- **Token Type**: Bearer Token
- **Algorithm**: HS256
- **Expiry**: 24 hours (86400000ms)
- **Header**: `Authorization: Bearer <jwt-token>`

### Protected Endpoints
- `/api/blogs` (except `/api/blogs/public/**`)
- `/api/chat/rooms` (POST operations only)
- `/api/chat/rooms/{roomId}/join`
- `/api/chat/rooms/{roomId}/leave`

### Public Endpoints
- `/api/auth/**`
- `/api/blogs/public/**`
- `/api/chat/rooms` (GET operations only)
- `/api/chat/rooms/{roomId}` (GET operations only)
- `/api/chat/rooms/{roomId}/members` (GET operations only)
- `/test**`
- Static resources (`/static/**`, `/websocket-demo.html`)

### WebSocket Security
- Session-based authentication
- STOMP protocol over WebSocket
- Cross-origin requests allowed for development

### CORS Configuration
- **Allowed Origins**: 
  - `http://localhost:3000` (React default)
  - `http://localhost:5173` (Vite default)
  - `http://localhost:8080` (Backend)
  - `http://127.0.0.1:5173` (Alternative localhost)
- **Allowed Methods**: All HTTP methods (GET, POST, PUT, DELETE, OPTIONS)
- **Allowed Headers**: All headers
- **Credentials**: Enabled (for JWT tokens)
- **Preflight Cache**: 1 hour

---

## 📊 Data Models

### User Model
```json
{
  "id": 1,
  "username": "john_doe"
}
```

### Blog Model
```json
{
  "id": 1,
  "title": "Blog Title",
  "content": "Blog content...",
  "user": {
    "id": 1,
    "username": "john_doe"
  }
}
```

### ChatMessage Model
```json
{
  "content": "Message content",
  "sender": "john_doe",
  "type": "CHAT|JOIN|LEAVE|TYPING",
  "timestamp": "2025-01-15T12:30:45",
  "room": "room-id"
}
```

### ChatRoom Model
```json
{
  "roomId": "general",
  "roomName": "General Discussion",
  "description": "General chat room",
  "createdBy": "admin",
  "createdAt": "2025-01-15T10:30:00",
  "private": false,
  "memberCount": 5
}
```

### NotificationMessage Model
```json
{
  "message": "Notification message",
  "type": "BLOG_CREATED|BLOG_UPDATED|BLOG_DELETED|USER_JOINED|SYSTEM",
  "sender": "john_doe",
  "timestamp": "2025-01-15T12:30:45",
  "data": {}
}
```

### UserPresence Model
```json
{
  "username": "john_doe",
  "online": true,
  "lastSeen": "2025-01-15T12:30:45",
  "status": "online|offline|typing|away"
}
```

---

## ⚠️ Error Handling

### Common HTTP Status Codes
- `200 OK`: Successful request
- `400 Bad Request`: Invalid request data or business logic error
- `401 Unauthorized`: Authentication required or invalid credentials
- `403 Forbidden`: Access denied (insufficient permissions)
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server error

### Error Response Formats

**String Error Response** (for most endpoints):
```json
"Error: Username already exists"
```

**LoginResponse Error** (for login endpoint):
```json
{
  "token": null,
  "type": "Bearer",
  "username": null,
  "expiresIn": 0
}
```

**404 Response** (for missing resources):
```
HTTP 404 Not Found
(empty body)
```

---

## 🌐 Static Resources

### Demo Pages
- **WebSocket Demo**: `http://localhost:8080/websocket-demo.html`
- **Simple Test**: `http://localhost:8080/simple-test.html`

### WebSocket Connection Example (JavaScript)
```javascript
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);
    
    // Subscribe to public chat
    stompClient.subscribe('/topic/public', function (message) {
        const chatMessage = JSON.parse(message.body);
        console.log('Received:', chatMessage);
    });
    
    // Send message
    stompClient.send('/app/chat.sendMessage', {}, JSON.stringify({
        content: 'Hello World!',
        sender: 'john_doe',
        type: 'CHAT'
    }));
});
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+
- Spring Boot 3.2.0

### Running the Application
1. Clone the repository
2. Configure MySQL database in `application.properties`
3. Run: `mvn clean install`
4. Start: `mvn spring-boot:run`
5. Access: `http://localhost:8080`

### Testing Endpoints
Use tools like:
- **Postman**: For REST API testing
- **WebSocket King**: For WebSocket testing
- **Browser**: For WebSocket demo pages

---

## � CORS Troubleshooting

### Frontend Integration Issues

**Problem**: `Access to XMLHttpRequest blocked by CORS policy`

**Solution**: The application now includes CORS configuration for common development ports:
- `http://localhost:3000` (React)
- `http://localhost:5173` (Vite)
- `http://localhost:8080` (Backend)
- `http://127.0.0.1:5173` (Alternative)

**Frontend Examples**:

**Basic Fetch Request**:
```javascript
fetch('http://localhost:8080/api/blogs/public')
  .then(response => response.json())
  .then(data => console.log(data))
  .catch(error => console.error('Error:', error));
```

**Authenticated Request**:
```javascript
fetch('http://localhost:8080/api/blogs/my-blogs', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
})
.then(response => response.json())
.then(data => console.log(data));
```

**POST Request with Authentication**:
```javascript
fetch('http://localhost:8080/api/blogs', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    title: 'New Blog Post',
    content: 'Blog content here...'
  })
})
.then(response => response.json())
.then(data => console.log(data));
```

---

## �📝 Notes

- All timestamps are in ISO 8601 format
- WebSocket connections are persistent
- JWT tokens should be stored securely on client side
- Real-time notifications are sent automatically on blog operations
- Room membership is tracked in memory (consider database for production)

---

**End of Documentation**

---

*This documentation covers all 36+ endpoints in the Spring Boot Blog application with real-time chat and notification features. *
