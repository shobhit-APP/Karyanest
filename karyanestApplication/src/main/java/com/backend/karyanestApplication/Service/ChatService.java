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
        Optional<Conversation> conversationOpt = conversationRepository.findById(conversationId);

        if (!conversationOpt.isPresent()) {
            result.put("canSend", false);
            result.put("reason", "Conversation not found");
            return result;
        }

        Conversation chat = conversationOpt.get();

        if (Conversation.ConversationStatus.CLOSED.equals(chat.getStatus())) {
            result.put("canSend", false);
            result.put("reason", "Chat is closed");
            return result;
        }

        boolean isParticipant = senderId.equals(chat.getInitiatorId()) ||
                senderId.equals(chat.getReceiverId()) ||
                (chat.getAssignedId() != null && senderId.equals(chat.getAssignedId()));

        boolean isAdmin = "ROLE_ADMIN".equalsIgnoreCase(senderRole);
        boolean isAgent = "ROLE_AGENT".equalsIgnoreCase(senderRole);

        if (!isParticipant && !isAdmin) {
            result.put("canSend", false);
            result.put("reason", "You are not authorized to send this message");
            return result;
        }

        if (chat.getAssignedId() != null && chat.getAssignedId().equals(senderId) && isAgent) {
            result.put("canSend", false);
            result.put("reason", "Agent is not allowed to send messages");
            return result;
        }

        result.put("canSend", true);
        result.put("reason", null);
        return result;
    }
    @Transactional
    public Map<String, Object> createChat(ChatRequest request, Long initiatorId, Long receiverId) {
        Conversation conversation;

        // 🔁 Reuse existing conversation if exists
        if (request.getType().equals("PROPERTY_INQUIRY")) {
            Optional<Conversation> existingConversation = conversationRepository
                    .findByPropertyIdAndInitiatorIdAndReceiverIdAndTypeAndStatus(
                            request.getPropertyId(),
                            initiatorId,
                            receiverId, // ✅ Default assigned user = admin
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
        conversation.setReceiverId(1L);
        conversation.setAssignedId(1L); // ✅ Default assigned user = admin
        // ✅ Always admin
        conversation.setPropertyOwnerId(request.getPropertyOwnerId());
        conversation.setType(Conversation.ConversationType.valueOf(request.getType()));
        conversation.setStatus(Conversation.ConversationStatus.OPEN);

        if (request.getType().equals("PROPERTY_INQUIRY")) {
            conversation.setPropertyId(request.getPropertyId());
        }

        return conversationRepository.save(conversation);
    }

    public List<Map<String, Object>> getAllConversationsForUser(Long userId) {
        // 🔁 Fetch conversations where the user is initiator, receiver, or assigned
        List<Conversation> conversations = conversationRepository.findByInitiatorIdOrReceiverIdOrAssignedId(userId, userId, userId);

        if (conversations.isEmpty()) return Collections.emptyList();

        // Collect all related userIds (initiator, receiver, assigned)
        Set<Long> userIds = conversations.stream()
                .flatMap(c -> Stream.of(c.getInitiatorId(), c.getReceiverId(), c.getAssignedId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        userIds.add(1L); // always include admin

        // Get user map
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
            Long assignedId = conv.getAssignedId();

            if (initiatorId == null && receiverId == null && assignedId == null) continue;

            // ✅ Skip if the user is not a participant (extra safety)
            if (!userId.equals(initiatorId) && !userId.equals(receiverId) && !userId.equals(assignedId)) continue;

            Message lastMessage = messageRepository.findTopByConversationIdOrderByTimestampDesc(conv.getId());

            Map<String, Object> conversationData = new HashMap<>();
            conversationData.put("conversationId", conv.getId());
            conversationData.put("propertyId", conv.getPropertyId() != null ? conv.getPropertyId() : -1);
            conversationData.put("status", conv.getStatus() != null ? conv.getStatus() : "unknown");
            conversationData.put("type", conv.getType() != null ? conv.getType() : "unknown");

            // ✅ Determine "other" participant to show as receiver
            User displayUser = null;

            if (!Objects.equals(userId, initiatorId) && userMap.containsKey(initiatorId)) {
                displayUser = userMap.get(initiatorId);
            } else if (!Objects.equals(userId, receiverId) && userMap.containsKey(receiverId)) {
                displayUser = userMap.get(receiverId);
            } else if (!Objects.equals(userId, assignedId) && userMap.containsKey(assignedId)) {
                displayUser = userMap.get(assignedId);
            } else {
                displayUser = userMap.get(1L); // fallback to admin
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
        Optional<Conversation> conversationOpt = conversationRepository.findById(conversationId);
        return conversationOpt.map(conversation ->
                conversation.getInitiatorId().equals(userId) ||
                        conversation.getReceiverId().equals(userId) ||
                        (conversation.getAssignedId() != null && conversation.getAssignedId().equals(userId))
        ).orElse(false);
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
    public Optional<Conversation> getConversationById(Long id) {
        return conversationRepository.findById(id);
    }

    public void saveConversation(Conversation conversation) {
        conversationRepository.save(conversation);
    }

}
