package com.backend.karyanestApplication.Controller;

import com.backend.karyanestApplication.DTO.ChatRequest;
import com.backend.karyanestApplication.DTO.MessageRequest;
import com.backend.karyanestApplication.Model.Message;
import com.backend.karyanestApplication.Service.ChatService;
import com.backend.karyanestApplication.Service.PropertyService;
import com.backend.karyanestApplication.Service.UserService;
import com.example.Authentication.Component.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/v1/chat")
public class ChatController {

    private final ChatService chatService;
    private final UserContext userContext;
    private final UserService userService;
    private final PropertyService propertyService;

    public ChatController(ChatService chatService, UserContext userContext, UserService userService,
                          PropertyService propertyService) {
        this.chatService = chatService;
        this.userContext = userContext;
        this.userService = userService;
        this.propertyService = propertyService;
    }
    private final Logger logger = LoggerFactory.getLogger(ChatController.class);
    // Start a chat - Only Admin or User with chat_start authority
    @PostMapping("/start")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER') or hasAuthority('chat_start')")
    public ResponseEntity<?> startChat(@RequestBody ChatRequest chatRequest, HttpServletRequest request) {
        Long userId = getUserId(request);
        String role = getUserRole(request);

        Long receiverId = 1L; // Default admin ID

        if ("PROPERTY_INQUIRY".equalsIgnoreCase(chatRequest.getType()) &&
                (chatRequest.getPropertyId() == null || !propertyService.existsById(chatRequest.getPropertyId()))) {
            return ResponseEntity.status(400).body("Invalid property ID for chat");
        }

        if (!chatService.isChatAllowed(userId, role)) {
            return ResponseEntity.status(403).body("Chat not allowed based on this role");
        }

        // Create chat with default admin ID
        return ResponseEntity.ok(chatService.createChat(chatRequest, userId, receiverId));
    }
//    @PostMapping("/send")
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER') or hasAuthority('chat_send')")
//    public ResponseEntity<?> sendMessage(@RequestBody MessageRequest messageRequest, HttpServletRequest request) {
//        Long userId = getUserId(request);
//        String role = getUserRole(request);
//
//        if (!chatService.canSendMessage(userId, messageRequest.getConversationId(), role)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(Map.of("error", "You are not authorized to send this message"));
//        }
//
//        Message message = chatService.sendMessage(messageRequest, userId, role);
//        if (message == null) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "Failed to send message"));
//        }
//
//        Long conversationId = message.getConversation() != null ? message.getConversation().getId() : null;
//        if (conversationId == null) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(Map.of("error", "Conversation ID is missing"));
//        }
//
//        List<Message> messages = chatService.getRawMessages(conversationId);
//        List<Map<String, Object>> chatMessages = new ArrayList<>();
//
//        Long previousSenderId = null;
//
//        for (Message msg : messages) {
//            Map<String, Object> map = new HashMap<>();
//            Long currentSenderId = msg.getSenderId();
//            String name = userService.findById(currentSenderId).getFullName();
//
//            if (previousSenderId == null || previousSenderId.equals(currentSenderId)) {
//                // Sender message
//                map.put("senderId", currentSenderId);
//                map.put("name", name);
//                map.put("message", msg.getMessage());
//                map.put("timestamp", msg.getTimestamp().toString());
//                map.put("IsSendstatus", true);
//            } else {
//                // Receiver response
//                map.put("reciverId", currentSenderId);
//                map.put("name", name);
//                map.put("messagerecieved", msg.getMessage());
//                map.put("timestamp", msg.getTimestamp().toString());
//                map.put("IsSendstatus", false);
//            }
//
//            previousSenderId = currentSenderId;
//            chatMessages.add(map);
//        }

    //        // Handle no reply case
//        Long receiverId = chatService.getsReceiverId(conversationId, userId);
//        boolean receiverReplied = chatMessages.stream()
//                .anyMatch(msg -> receiverId != null && receiverId.equals(msg.get("senderId")));
//
//        if (!receiverReplied && receiverId != null) {
//            Map<String, Object> noReplyMsg = new HashMap<>();
//            User receiver = userService.findById(receiverId);
//            noReplyMsg.put("reciverId", receiverId);
//            noReplyMsg.put("name", receiver.getFullName());
//            noReplyMsg.put("messagerecieved", "No response yet.");
//            noReplyMsg.put("timestamp", null);
//            noReplyMsg.put("IsSendstatus", false);
//            chatMessages.add(noReplyMsg);
//        }
//
//        return ResponseEntity.ok(chatMessages);
//    }
//@PostMapping("/send")
//@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER') or hasAuthority('chat_send')")
//public ResponseEntity<?> sendMessage(@RequestBody MessageRequest messageRequest, HttpServletRequest request) {
//    Long userId = getUserId(request);
//    String role = getUserRole(request);
//
//    if (!chatService.canSendMessage(userId, messageRequest.getConversationId(), role)) {
//        return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                .body(Map.of("error", "You are not authorized to send this message"));
//    }
//
//    Message message = chatService.sendMessage(messageRequest, userId, role);
//    if (message == null) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(Map.of("error", "Failed to send message"));
//    }
//
//    Long conversationId = message.getConversation() != null ? message.getConversation().getId() : null;
//    if (conversationId == null) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(Map.of("error", "Conversation ID is missing"));
//    }
//
//    List<Message> messages = chatService.getRawMessages(conversationId);
//    List<Map<String, Object>> chatMessages = new ArrayList<>();
//
//    Long lastSenderMessageIndex = null;
//
//    for (Message msg : messages) {
//        Map<String, Object> map = new HashMap<>();
//        Long currentSenderId = msg.getSenderId();
//        String name = userService.findById(currentSenderId).getFullName();
//
//        map.put("name", name);
//        map.put("timestamp", msg.getTimestamp().toString());
//
//        if (currentSenderId.equals(userId)) {
//            // Logged-in user's message
//            map.put("senderId", currentSenderId);
//            map.put("message", msg.getMessage());
//            map.put("IsSendstatus", true);
//
//            lastSenderMessageIndex = (long) chatMessages.size(); // Save index for later update
//        } else {
//            // Receiver's reply
//            map.put("reciverId", currentSenderId);
//            map.put("messagerecieved", msg.getMessage());
//            map.put("IsSendstatus", false);
//
//            // Update last sender message to false
//            if (lastSenderMessageIndex != null) {
//                Map<String, Object> prevMap = chatMessages.get(lastSenderMessageIndex.intValue());
//                prevMap.put("IsSendstatus", false);
//                lastSenderMessageIndex = null; // Reset after updating
//            }
//        }
//
//        chatMessages.add(map);
//    }
//
//    return ResponseEntity.ok(chatMessages);
//}
    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER') or hasAuthority('chat_send')")
    public ResponseEntity<?> sendMessage(@RequestBody MessageRequest messageRequest, HttpServletRequest request) {
        Long userId = getUserId(request);
        String role = getUserRole(request);

        Map<String, Object> sendResult = chatService.canSendMessage(userId, messageRequest.getConversationId(), role);
        if (!(Boolean) sendResult.get("canSend")) {
            String reason = (String) sendResult.get("reason");
            logger.warn("Message send failed for userId: {}, conversationId: {}, reason: {}", userId, messageRequest.getConversationId(), reason);
            if ("Chat is closed".equals(reason)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Chat is closed"));
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", reason));
        }

        Message message = chatService.sendMessage(messageRequest, userId, role);
        if (message == null) {
            logger.error("Internal error sending message for userId: {}, conversationId: {}", userId, messageRequest.getConversationId());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to send message"));
        }

        Long conversationId = message.getConversation() != null ? message.getConversation().getId() : null;
        if (conversationId == null) {
            logger.error("Missing conversation ID for message sent by userId: {}", userId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Conversation ID is missing"));
        }

        // ⏰ Check timestamp in request (optional)
        List<Message> messages;
        if (messageRequest.getTimestamp() != null) {
            messages = chatService.getMessagesAfterTimestamp(conversationId, messageRequest.getTimestamp());
        } else {
            messages = chatService.getRawMessages(conversationId);
        }

        // 🔁 Prepare response
        List<Map<String, Object>> chatMessages = new ArrayList<>();
        for (Message msg : messages) {
            Map<String, Object> map = new HashMap<>();
            Long senderId = msg.getSenderId();
            String name = userService.findById(senderId).getFullName();

            map.put("userId", senderId);
            map.put("name", name);
            map.put("message", msg.getMessage());
            map.put("timestamp", msg.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            chatMessages.add(map);
        }

        return ResponseEntity.ok(chatMessages);
    }




    // Fetch all messages from a conversation - Only Admin or User with chat_view
    // authority
    @GetMapping("/conversations")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER') or hasAuthority('chat_view')")
    public ResponseEntity<?> getAllConversations(HttpServletRequest request) {
        Long userId = getUserId(request);
        List<Map<String, Object>> conversations = chatService.getAllConversationsForUser(userId);
        return ResponseEntity.ok(conversations);
    }

    // Stub method — replace with actual logic (JWT, session, etc.)
    // Close a chat - Only Admin or Owner can close
    @PostMapping("/close/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER') or hasAuthority('chat_close')")
    public ResponseEntity<?> closeChat(@PathVariable Long id, HttpServletRequest request) {
        String role = getUserRole(request);

        // Only Admin or Owner can close a chat
        if (!role.equals("ROLE_ADMIN") && !role.equals("ROLE_OWNER")) {
            return ResponseEntity.status(403).body("Only Admin or Owner can close a chat");
        }

        chatService.closeConversation(id);
        return ResponseEntity.ok("Chat closed successfully");
    }

    // Extract user ID from JWT authentication
    private Long getUserId(HttpServletRequest request) {
        String username = userContext.getUsername(request);
        return userService.getUserIdByUsername(username); // Assumes user ID is stored in JWT subject
    }

    // Extract user role from JWT authentication
    private String getUserRole(HttpServletRequest request) {
        String username = userContext.getUsername(request);
        return userContext.getUserRole(request);
    }
    @GetMapping("/conversation/{conversationId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER') or hasAuthority('refresh_chat')")
    public ResponseEntity<?> getConversationMessages(
            @PathVariable Long conversationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime timestamp,
            HttpServletRequest request) {

        Long userId = getUserId(request);

        // ✅ Check if user is allowed to access this conversation
        if (!chatService.isParticipant(conversationId, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You are not authorized to view this conversation"));
        }

        // ✅ Fetch messages
        List<Message> messages = (timestamp != null)
                ? chatService.getMessagesAfterTimestamp(conversationId, timestamp)
                : chatService.getRawMessages(conversationId);

        // ✅ Build response
        List<Map<String, Object>> chatMessages = new ArrayList<>();
        for (Message msg : messages) {
            Map<String, Object> map = new HashMap<>();
            Long senderId = msg.getSenderId();
            String name = userService.findById(senderId).getFullName();

            map.put("userId", senderId);
            map.put("name", name);
            map.put("message", msg.getMessage());
            map.put("timestamp", msg.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            chatMessages.add(map);
        }

        return ResponseEntity.ok(chatMessages);
    }


}
