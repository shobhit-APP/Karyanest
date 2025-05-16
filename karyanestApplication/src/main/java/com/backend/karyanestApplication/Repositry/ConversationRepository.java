package com.backend.karyanestApplication.Repositry;

import com.backend.karyanestApplication.Model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    boolean existsByInitiatorIdOrReceiverIdAndId(Long initiatorId, Long receiverId, Long conversationId);
    List<Conversation> findByInitiatorIdOrReceiverId(Long initiatorId, Long receiverId);
    List<Conversation> findByInitiatorId(Long initiatorId);
    Optional<Conversation> findByPropertyIdAndInitiatorIdAndReceiverIdAndTypeAndStatus(Long propertyId, Long initiatorId, Long receiverId, Conversation.ConversationType conversationType, Conversation.ConversationStatus conversationStatus);
}
