package com.example.conversationms.Persistence.Message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageDbRepository extends JpaRepository<MessageDb,Long> {
}
