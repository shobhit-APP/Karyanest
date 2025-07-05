package com.backend.karyanestApplication.Service;

import com.backend.karyanestApplication.DTO.ChatRequest;
import com.backend.karyanestApplication.DTO.MessageRequest;
import com.backend.karyanestApplication.Model.Conversation;
import com.backend.karyanestApplication.Model.Message;
import com.backend.karyanestApplication.Model.User;
import com.backend.karyanestApplication.Repositry.ConversationRepository;
import com.backend.karyanestApplication.Repositry.MessageRepository;
import com.backend.karyanestApplication.Repositry.PropertyRepository;
import com.backend.karyanestApplication.Repositry.UserRepo;
import com.example.rbac.Service.RolesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ChatService {

    @Autowired
    private UserService userService;

    @Autowired
    private RolesService roleService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;
    private final Logger logger = LoggerFactory.getLogger(ChatService.class);
    public boolean isChatAllowed(Long Id, String senderRole) {
        // Block User ↔ Agent communication for property-related chat
        return !senderRole.equals("ROLE_USER") || !isAgent(Id);
    }
    public Map<String, Object> canSendMessage(Long senderId, Long conversationId, String senderRole) {
        Map<String, Object> result = new HashMap<>();
        Optional<Conversation> conversation = conversationRepository.findById(conversationId);

        if (!conversation.isPresent()) {
            logger.warn("Conversation not found for conversationId: {}", conversationId);
            result.put("canSend", false);
            result.put("reason", "Conversation not found");
            return result;
        }

        Conversation chat = conversation.get();

        // Check if chat is closed
        if (Conversation.ConversationStatus.CLOSED.equals(chat.getStatus())) {
            logger.info("Chat closed for conversationId: {}, senderId: {}", conversationId, senderId);
            result.put("canSend", false);
            result.put("reason", "Chat is closed");
            return result;
        }

        // Check if sender is participant or admin
        boolean isParticipant = (chat.getInitiatorId() != null && chat.getInitiatorId().equals(senderId)) ||
                (chat.getReceiverId() != null && chat.getReceiverId().equals(senderId));
        boolean isAdmin = "ROLE_ADMIN".equalsIgnoreCase(senderRole);

        if (!isParticipant && !isAdmin) {
            logger.warn("Unauthorized attempt to send message, senderId: {}, conversationId: {}", senderId, conversationId);
            result.put("canSend", false);
            result.put("reason", "You are not authorized to send this message");
            return result;
        }

        result.put("canSend", true);
        result.put("reason", null);
        return result;
    }
    //   @Transactional
//    public Conversation createChat(ChatRequest request, Long InitiatorId, Long ReceiverId) {
//        Conversation conversation = new Conversation();
//        conversation.setInitiatorId(InitiatorId);
//        conversation.setReceiverId(ReceiverId);
//        conversation.setPropertyOwnerId(request.getPropertyOwnerId());
//        conversation.setType(Conversation.ConversationType.valueOf(request.getType()));
//        conversation.setStatus(Conversation.ConversationStatus.OPEN);
//        if (request.getType().equals("PROPERTY_INQUIRY")) {
//            conversation.setPropertyId(request.getPropertyId());  // 🔥 Property ID Set Karo
//        }
//        return conversationRepository.save(conversation);
//    }
//@Transactional
//public Conversation createChat(ChatRequest request, Long initiatorId, Long receiverId) {
//    // Only for PROPERTY_INQUIRY
//    if (request.getType().equals("PROPERTY_INQUIRY")) {
//        Optional<Conversation> existingConversation = conversationRepository
//                .findByPropertyIdAndInitiatorIdAndReceiverIdAndTypeAndStatus(
//                        request.getPropertyId(),
//                        initiatorId,
//                        receiverId,
//                        Conversation.ConversationType.valueOf(request.getType()),
//                        Conversation.ConversationStatus.OPEN
//                );
//
//        if (existingConversation.isPresent()) {
//            return existingConversation.get(); // 🔁 Return existing instead of creating new
//        }
//    }
//
//    // ⬇️ Create new if not found
//    Conversation conversation = new Conversation();
//    conversation.setInitiatorId(initiatorId);
//    conversation.setReceiverId(receiverId);
//    conversation.setPropertyOwnerId(request.getPropertyOwnerId());
//    conversation.setType(Conversation.ConversationType.valueOf(request.getType()));
//    conversation.setStatus(Conversation.ConversationStatus.OPEN);
//
//    if (request.getType().equals("PROPERTY_INQUIRY")) {
//        conversation.setPropertyId(request.getPropertyId());
//    }
//
//    return conversationRepository.save(conversation);
//}
    @Transactional
    public Map<String, Object> createChat(ChatRequest request, Long initiatorId, Long receiverId) {
        Conversation conversation;

        // 🔁 Reuse existing conversation if exists
        if (request.getType().equals("PROPERTY_INQUIRY")) {
            Optional<Conversation> existingConversation = conversationRepository
                    .findByPropertyIdAndInitiatorIdAndReceiverIdAndTypeAndStatus(
                            request.getPropertyId(),
                            initiatorId,
                            receiverId,
                            Conversation.ConversationType.valueOf(request.getType()),
                            Conversation.ConversationStatus.OPEN
                    );

            conversation = existingConversation.orElseGet(() -> createNewConversation(request, initiatorId, receiverId));
        } else {
            conversation = createNewConversation(request, initiatorId, receiverId);
        }

        // 📥 Fetch property owner details from user service/repository
        User propertyOwner = userService.findById(conversation.getPropertyOwnerId());

        Map<String, Object> response = new HashMap<>();
        response.put("conversationId", conversation.getId());
        response.put("type", conversation.getType().toString());
        response.put("status", conversation.getStatus().toString());
        response.put("propertyId", conversation.getPropertyId());
        response.put("propertyOwnerId", conversation.getPropertyOwnerId());
        response.put("propertyOwnerName", propertyOwner.getFullName());
        response.put("propertyOwnerProfile", propertyOwner.getProfilePicture()); // Assuming you have this field

        return response;
    }
    private Conversation createNewConversation(ChatRequest request, Long initiatorId, Long receiverId) {
        Conversation conversation = new Conversation();
        conversation.setInitiatorId(initiatorId);
        conversation.setReceiverId(1L); // ✅ Always admin
        conversation.setPropertyOwnerId(request.getPropertyOwnerId());
        conversation.setType(Conversation.ConversationType.valueOf(request.getType()));
        conversation.setStatus(Conversation.ConversationStatus.OPEN);

        if (request.getType().equals("PROPERTY_INQUIRY")) {
            conversation.setPropertyId(request.getPropertyId());
        }

        return conversationRepository.save(conversation);
    }



//
//    public ResponseEntity<List<Map<String, Object>>> getMessages(Long conversationId) {
//        List<Message> messages = messageRepository.findByConversationId(conversationId);
//
//        if (messages == null || messages.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(Collections.emptyList());
//        }
//
//        Set<Long> participants = messages.stream()
//                .map(Message::getSenderId)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toSet());
//
//        if (participants.size() < 2) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(Collections.emptyList());
//        }
//
//        // ✅ Format messages with name and status
//        List<Map<String, Object>> formattedMessages = messages.stream().map(msg -> {
//            Map<String, Object> map = new HashMap<>();
//            map.put("senderId", msg.getSenderId());
//            map.put("message", msg.getMessage() != null ? msg.getMessage() : "No message content");
//            map.put("timestamp", msg.getTimestamp() != null ? msg.getTimestamp().toString() : null);
//
//            // ✅ Add sender name
//               User user= userService.findById(msg.getSenderId()); // You need to implement this method
//               map.put("name", user.getFullName());
//
//            // ✅ Add status
//            map.put("status", true);
//
//            return map;
//        }).collect(Collectors.toList());
//
//
//
//        return ResponseEntity.ok(formattedMessages);
//    }

    public List<Map<String, Object>> getAllConversationsForUser(Long userId) {
        List<Conversation> conversations = conversationRepository.findByInitiatorIdOrReceiverId(userId, userId);

        if (conversations.isEmpty()) return Collections.emptyList();

        // Collect all user IDs involved in the conversations
        Set<Long> userIds = conversations.stream()
                .flatMap(c -> Stream.of(c.getInitiatorId(), c.getReceiverId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        userIds.add(1L); // ensure user with ID=1 (admin) is included

        List<User> users = userRepo.findAllById(userIds);
        Map<Long, User> userMap = users.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(User::getId, u -> u));

        List<Map<String, Object>> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        for (Conversation conv : conversations) {
            if (conv == null) continue;

            Long initiatorId = conv.getInitiatorId();
            Long receiverId = conv.getReceiverId();

            if (initiatorId == null || receiverId == null) continue;

            // Filter logic
            if (userId != 1 && !Objects.equals(initiatorId, userId)) continue;

            User initiator = userMap.get(initiatorId);
            User receiver = userMap.get(receiverId);
            if (initiator == null || receiver == null) continue;

            Message lastMessage = messageRepository.findTopByConversationIdOrderByTimestampDesc(conv.getId());

            Map<String, Object> conversationData = new HashMap<>();
            conversationData.put("conversationId", conv.getId());
            conversationData.put("propertyId", conv.getPropertyId() != null ? conv.getPropertyId() : -1);
            conversationData.put("status", conv.getStatus() != null ? conv.getStatus() : "unknown");
            conversationData.put("type", conv.getType() != null ? conv.getType() : "unknown");

            // ✅ Receiver logic based on userId
            User displayUser;
            if (userId == 1L) {
                displayUser = initiator;
            } else {
                displayUser = userMap.get(1L); // always show admin user
            }

            conversationData.put("receiver", Map.of(
                    "id", displayUser.getId(),
                    "name", displayUser.getFullName() != null ? displayUser.getFullName() : "Unknown",
                    "profileImage", displayUser.getProfilePicture() != null ? displayUser.getProfilePicture() : ""
            ));

            if (lastMessage != null) {
                conversationData.put("lastMessage", lastMessage.getMessage() != null ? lastMessage.getMessage() : "");
                conversationData.put("lastMessageTime", lastMessage.getTimestamp() != null
                        ? lastMessage.getTimestamp().format(formatter)
                        : null);
            } else {
                conversationData.put("lastMessage", "No messages yet.");
                conversationData.put("lastMessageTime", null);
            }

            result.add(conversationData);
        }

        return result;
    }
    @Transactional
    public Message sendMessage(MessageRequest messageRequest, Long userId, String role) {
        Conversation conversation = conversationRepository.findById(messageRequest.getConversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        Message message = new Message();
        message.setConversation(conversation);
        message.setSenderId(userId);
        message.setMessage(messageRequest.getMessage());

        if (role.equals("ROLE_ADMIN")) {
            updateReceiverIdIfAdminJoins(userId, conversation);
        } else if (role.equals("ROLE_AGENT")) {
            keepReceiverAsAdmin(conversation); // Always admin
        }

        return messageRepository.save(message);
    }

    @Transactional
    public void updateReceiverIdIfAdminJoins(Long adminId, Conversation conversation) {
        Long initiatorId = conversation.getInitiatorId();
        Long currentReceiverId = conversation.getReceiverId();

        // Only update if admin is not initiator AND not already receiver
        if (!adminId.equals(initiatorId) && !adminId.equals(currentReceiverId)) {
            conversation.setReceiverId(adminId);
            conversationRepository.save(conversation);
        }
    }

    @Transactional
    public void keepReceiverAsAdmin(Conversation conversation) {
        if (conversation.getReceiverId() != 1L) {
            conversation.setReceiverId(1L); // Always admin
            conversationRepository.save(conversation);
        }
    }


    private boolean isAgent(Long userId) {
        User user = userService.findById(userId);
        return user.getRole().getName().equals("ROLE_AGENT");
    }

    public boolean isUserPartOfConversation(Long userId, Long conversationId) {
        Optional<Conversation> conversationOpt = conversationRepository.findById(conversationId);
        return conversationOpt.map(conversation ->
                        conversation.getInitiatorId().equals(userId) || conversation.getReceiverId().equals(userId))
                .orElse(false);
    }

    public void closeConversation(Long conversationId) {
        Optional<Conversation> conversationOpt = conversationRepository.findById(conversationId);

        if (conversationOpt.isPresent()) {
            Conversation conversation = conversationOpt.get();
            conversation.setStatus(Conversation.ConversationStatus.CLOSED);
            conversationRepository.save(conversation);
        } else {
            throw new RuntimeException("Conversation not found.");
        }
    }
    @Transactional
    public List<Message> getRawMessages(Long conversationId) {
        return messageRepository.findByConversationId(conversationId);
    }

    public Long getsReceiverId(Long conversationId, Long senderId) {
        // Find a participant other than the sender
        List<Long> participantIds = messageRepository.findParticipantIdsByConversationId(conversationId);
        return participantIds.stream()
                .filter(id -> !id.equals(senderId))
                .findFirst()
                .orElse(null);
    }
    public boolean isParticipant(Long conversationId, Long userId) {
        return conversationRepository.existsByInitiatorIdOrReceiverIdAndId(userId,userId,conversationId);
    }

    public List<Message> getMessagesAfterTimestamp(Long conversationId, ZonedDateTime timestamp) {
        if (timestamp == null) {
            // If no timestamp is provided, fetch all messages of that conversation
            return messageRepository.findByConversationIdOrderByTimestampAsc(conversationId);
        } else {
            // Fetch only messages sent after the given timestamp
            return messageRepository.findByConversationIdAndTimestampAfterOrderByTimestampAsc(conversationId, timestamp);
        }
    }

}
