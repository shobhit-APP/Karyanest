package com.backend.karyanestApplication.Controller;

import com.backend.karyanestApplication.DTO.ChatRequest;
import com.backend.karyanestApplication.DTO.MessageRequest;
import com.backend.karyanestApplication.Model.Message;
import com.backend.karyanestApplication.Service.ChatService;
import com.backend.karyanestApplication.Service.PropertyService;
import com.backend.karyanestApplication.Service.UserService;
import com.example.Authentication.Component.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

    // Send message - Only Admin or User with chat_send authority
    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER') or hasAuthority('chat_send')")
    public ResponseEntity<?> sendMessage(@RequestBody MessageRequest messageRequest, HttpServletRequest request) {
        Long userId = getUserId(request);
        String role = getUserRole(request);

        // ✅ Authorization Check
        if (!chatService.canSendMessage(userId, messageRequest.getConversationId(), role)) {
            return ResponseEntity.status(403).body("You are not authorized to send this message");
        }

        // ✅ Send Message
        Message message = chatService.sendMessage(messageRequest, userId, role);
        if (message == null) {
            return ResponseEntity.status(500).body("Failed to send message");
        }

        Long conversationId = message.getConversation().getId();
        Long senderId = message.getSenderId();

        // ✅ Fetch chat history
        ResponseEntity<List<String>> chatMessagesResponse = chatService.getMessages(conversationId);
        List<String> chatMessages = chatMessagesResponse.getBody();

        if (chatMessages == null) {
            chatMessages = new ArrayList<>();
        }

        // ✅ Ensure unique chat history (Avoid Duplicates)
        Set<String> uniqueMessages = new LinkedHashSet<>(chatMessages);

        // ✅ Construct Chat History
        StringBuilder fullChatHistory = new StringBuilder("Chat History:\n");
        for (String chatMessage : uniqueMessages) {
            fullChatHistory.append(chatMessage).append("\n");
        }

        // ✅ Format new message with timestamp
        String newMessage = "User " + senderId + " [" + message.getTimestamp() + "]: " + messageRequest.getMessage();

        // ✅ Add only if it does NOT already exist
        if (!uniqueMessages.contains(newMessage)) {
            fullChatHistory.append(newMessage).append("\n");
        }

        // ✅ Get receiverId
        Long receiverId = chatService.getReceiverId(conversationId, senderId);

        // ✅ Ensure "No response yet" message appears correctly
        boolean receiverReplied = uniqueMessages.stream()
                .anyMatch(msg -> msg.startsWith("User " + receiverId + ":"));

        if (!receiverReplied) {
            fullChatHistory.append("User ").append(receiverId != null ? receiverId : "null")
                    .append(": No response yet.\n");
        }

        return ResponseEntity.ok(fullChatHistory.toString().trim());
    }

    // Fetch all messages from a conversation - Only Admin or User with chat_view
    // authority
    @GetMapping("/messages/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER') or hasAuthority('chat_view')")
    public ResponseEntity<List<String>> getMessages(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserId(request);

        // Ensure user is part of this conversation
        if (!chatService.isUserPartOfConversation(userId, id)) {
            return ResponseEntity.status(403).body(null);
        }

        return ResponseEntity.ok(chatService.getMessages(id).getBody());
    }

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
}
