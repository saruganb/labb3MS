package com.example.conversationms.Persistence.Message;

import com.example.conversationms.Core.Message;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity(name = "messageDb")
@Table
public class MessageDb {

    @Id
    @SequenceGenerator(name="message_sequence", sequenceName = "message_sequence", allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE, generator = "message_sequence")
    private Long messageId;
    @Column(name = "body", updatable = false, columnDefinition = "TEXT", length = 300)
    private String body;
    @Column(name = "date", updatable = false, nullable = false)
    private LocalDateTime date;
    @Column(name = "sender", updatable = false, nullable = false)
    private String sender;
    @Column(name = "receiver", updatable = false, nullable = false)
    private String receiver;
    @Column(name="image", length = 100000)
    private byte[] image;

    public MessageDb(Long messageId, String body, LocalDateTime date, String sender, String receiver, byte[] image) {
        this.messageId = messageId;
        this.body = body;
        this.date = date;
        this.sender = sender;
        this.receiver = receiver;
        this.image = image;
    }

    public MessageDb(String body, LocalDateTime date, String sender, String receiver, byte[] image) {
        this.body = body;
        this.date = date;
        this.sender = sender;
        this.receiver = receiver;
        this.image = image;
    }

    public MessageDb(String body, LocalDateTime date, String sender, String receiver) {
        this.body = body;
        this.date = date;
        this.sender = sender;
        this.receiver = receiver;
    }

    public void setImage(byte[] image){
        this.image = image;
    }

    public byte [] getImage(){
        return this.image;
    }

    public MessageDb(){}

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

    public static MessageDb toMessageDb(Message m){
        return new MessageDb(m.getBody(),m.getDate(),m.getSender(),m.getReceiver(),m.getImage());
    }

    @Override
    public String toString() {
        return "MessageDb{" +
                "messageId=" + messageId +
                ", body='" + body + '\'' +
                ", date=" + date +
                ", sender=" + sender +
                ", receiver=" + receiver +
                '}';
    }
}

