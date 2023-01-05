package com.example.conversationms.Persistence.Conversation;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationDbRepository extends JpaRepository<ConversationDb,Long> {

    @Query(value = "SELECT c FROM conversationDb c where c.participant1 = ?1 AND c.participant2 = ?2 OR c.participant1 = ?2 AND c.participant2 = ?1")
    Optional<ConversationDb> getConversationDbByParticipantIds(String p1, String p2);
}
