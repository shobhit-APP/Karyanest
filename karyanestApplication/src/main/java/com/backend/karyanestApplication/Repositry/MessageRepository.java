package com.backend.karyanestApplication.Repositry;

import com.backend.karyanestApplication.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationId(Long conversationId);
    Message findTopByConversationIdOrderByTimestampDesc(Long id);
    @Query("SELECT DISTINCT m.senderId FROM Message m WHERE m.conversation.id = :conversationId")
    List<Long> findParticipantIdsByConversationId(Long conversationId);
    List<Message> findByConversationIdOrderByTimestampAsc(Long conversationId);
    List<Message> findByConversationIdAndTimestampAfterOrderByTimestampAsc(Long conversationId, ZonedDateTime timestamp);
}
