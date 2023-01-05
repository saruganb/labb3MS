package com.example.conversationms.Persistence.Conversation;

import com.example.conversationms.Core.Conversation;
import com.example.conversationms.Core.ImageCompress;
import com.example.conversationms.Core.Message;
import com.example.conversationms.Persistence.Message.MessageDb;
import org.keycloak.representations.IDToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConversationDbService {
    private final ConversationDbRepository conversationDbRepository;

    @Autowired
    public ConversationDbService(ConversationDbRepository conversationDbRepository) {
        this.conversationDbRepository = conversationDbRepository;
    }

    public Conversation sendMessage(String participant1, String participant2, Message message, String accessToken){
        if(participant1.equals(participant2)) throw new IllegalStateException("Illegal action");
        else if(!getUser(participant1,accessToken)) throw new IllegalStateException("No user with id" +  participant1 + " exists");
        else if(!getUser(participant2,accessToken)) throw new IllegalStateException("No user with id" +  participant2 + " exists");

        Optional<ConversationDb> conversationDb = conversationDbRepository.getConversationDbByParticipantIds(participant1,participant2);

        ConversationDb cDb;
        if(conversationDb.isEmpty()){
            cDb = addMessage(createNewConversationDb(participant1, participant2), message);
        }
        else {
            cDb = addMessage(conversationDb.get(), message);
        }
        return conversationHistory(cDb);
    }

    public Conversation getConversation(String participant1, String participant2){
        Optional<ConversationDb> conversationDb = conversationDbRepository.getConversationDbByParticipantIds(participant1,participant2);

        if(conversationDb.isEmpty()) return null;

        Conversation conversation = Conversation.toConversation(conversationDb.get());

        List<MessageDb> messageDbs = conversationDb.get().getMessageDbs();
        List<Message> messages = new ArrayList<>();

        for(int i = 0; i < messageDbs.size(); i++){
            messages.add(Message.toMessage(messageDbs.get(i)));
        }
        conversation.addMessages(messages);

        return conversation;
    }



    private boolean getUser(String id, String accessToken){
        try{
            final String uri = "http://localhost:8003/api/v1/user/get/" + id;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Object> response = restTemplate.exchange(uri, HttpMethod.GET, entity, Object.class);

            //System.out.println(response.getBody());

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private Conversation conversationHistory(ConversationDb conversationDb){
        Conversation conversation = Conversation.toConversation(conversationDb);

        for(int i = 0; i < conversationDb.getMessageDbs().size(); i++){
            conversation.addMessage(Message.toMessage(conversationDb.getMessageDbs().get(i)));
        }

        return conversation;
    }

    private ConversationDb createNewConversationDb(String participant1, String participant2){
        return new ConversationDb(participant1,participant2);
    }
    private ConversationDb addMessage(ConversationDb conversationDb, Message message){
        conversationDb.addMessage(MessageDb.toMessageDb(message));
        conversationDbRepository.save(conversationDb);

        return conversationDb;
    }

}
