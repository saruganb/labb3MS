package com.example.conversationms.Persistence.Conversation;

import com.example.conversationms.Core.Conversation;
import com.example.conversationms.Persistence.Message.MessageDb;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity(name = "conversationDb")
@Table
public class ConversationDb {

    @Id
    @SequenceGenerator(name="conversation_sequence", sequenceName = "conversation_sequence", allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE, generator = "conversation_sequence")
    @Column(name = "conversationId", updatable = false)
    private Long conversationId;
    @Column(name = "participant1", updatable = false, nullable = false)
    private String participant1;
    @Column(name = "participant2", updatable = false, nullable = false)
    private String participant2;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "conversation_id_fk", referencedColumnName = "conversationId")
    private List<MessageDb> messageDbs = new ArrayList<>();

    public ConversationDb(Long conversationId, String participant1, String participant2) {
        this.conversationId = conversationId;
        this.participant1 = participant1;
        this.participant2 = participant2;
    }

    public ConversationDb(String participant1, String participant2) {
        this.participant1 = participant1;
        this.participant2 = participant2;
    }

    public ConversationDb(){}

    public List<MessageDb> getMessageDbs(){
        return messageDbs;
    }

    public void addMessage(MessageDb messageDb){
        messageDbs.add(messageDb);
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public String getParticipant1() {
        return participant1;
    }

    public void setParticipant1(String participant1) {
        this.participant1 = participant1;
    }

    public String getParticipant2() {
        return participant2;
    }

    public void setParticipant2(String participant2) {
        this.participant2 = participant2;
    }


    @Override
    public String toString() {
        return "ConversationDb{" +
                "conversationId=" + conversationId +
                ", participant1=" + participant1 +
                ", participant2=" + participant2 +
                ", messageDbs=" + messageDbs +
                '}';
    }
}

