package com.example.conversationms.Core;

import com.example.conversationms.Persistence.Conversation.ConversationDb;
import com.example.conversationms.Persistence.Message.MessageDb;

import java.util.ArrayList;
import java.util.List;

public class Conversation {
    private Long conversationId;
    private String participant1;
    private String participant2;
    private List<Message> messages = new ArrayList<>();

    public Conversation(Long conversationId, String participant1, String participant2) {
        this.conversationId = conversationId;
        this.participant1 = participant1;
        this.participant2 = participant2;
    }

    public Conversation(String participant1, String participant2) {
        this.participant1 = participant1;
        this.participant2 = participant2;
    }

    public Conversation(){}


    public List<Message> getMessages(){
        return messages;
    }

    public void addMessage(Message message){
        messages.add(message);
    }

    public void addMessages(List<Message> messages){
        this.messages.addAll(messages);
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

    public static Conversation toConversation(ConversationDb c){
       return new Conversation(c.getConversationId(),c.getParticipant1(), c.getParticipant2());
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "conversationId=" + conversationId +
                ", senderId=" + participant1 +
                ", receiverId=" + participant2 +
                '}';
    }
}
