package com.example.conversationms.Core;

import com.example.conversationms.Persistence.Message.MessageDb;

import java.time.LocalDateTime;

public class Message {
    private Long messageId;
    private String body;
    private LocalDateTime date;
    private String sender;
    private String receiver;
    private byte[] image;

    public Message(Long messageId, String body, LocalDateTime date, String sender, String receiver, byte[] image){
        this.messageId = messageId;
        this.body = body;
        this.date = date;
        this.sender = sender;
        this.receiver = receiver;
        this.image = image;
    }
    public Message(String body, LocalDateTime date, String sender, String receiver, byte[] image){
        this.body = body;
        this.date = date;
        this.sender = sender;
        this.receiver = receiver;
        this.image = image;
    }
    public Message(Long messageId, String body, LocalDateTime date, String sender, String receiver){
        this.messageId = messageId;
        this.body = body;
        this.date = date;
        this.sender = sender;
        this.receiver = receiver;
    }
    public Message(String body, LocalDateTime date, String sender, String receiver){
        this.body = body;
        this.date = date;
        this.sender = sender;
        this.receiver = receiver;
    }
    public Message(LocalDateTime date, String sender, String receiver){
        this.date = date;
        this.sender = sender;
        this.receiver = receiver;
    }
    public Message() {}

    public void setImage(byte[] image){
        this.image = image;
    }

    public byte [] getImage(){
        return this.image;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public static Message toMessage(MessageDb m){
        if(m.getImage() != null) return new Message(m.getMessageId(), m.getBody(), m.getDate(), m.getSender(), m.getReceiver(),ImageCompress.decompressBytes(m.getImage()));

        return new Message(m.getMessageId(), m.getBody(), m.getDate(), m.getSender(), m.getReceiver());
    }

    @Override
    public String toString() {
        return "Message{" +
                "MessageId=" + messageId +
                ", body='" + body + '\'' +
                ", date=" + date +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                '}';
    }
}
