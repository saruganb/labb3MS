package com.example.conversationms.Core;

import com.example.conversationms.Persistence.Conversation.ConversationDbService;
import com.example.conversationms.Persistence.Message.MessageDbService;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;

@RestController
@CrossOrigin
@RequestMapping(path = "api/v1/conversation")
public class ConversationController {
    private final MessageDbService messageDbService;
    private final ConversationDbService conversationDbService;

    @Autowired
    public ConversationController(MessageDbService messageDbService, ConversationDbService conversationDbService) {
        this.messageDbService = messageDbService;
        this.conversationDbService = conversationDbService;
    }

    @PostMapping(value = "sendMessage/{senderId}/{receiverId}/{message}")
    @ResponseBody
    @RolesAllowed("user")
    public Conversation sendMessage(@PathVariable("senderId") String senderId, @PathVariable("receiverId") String receiverId, @RequestBody(required = false) MultipartFile image, @PathVariable(required = false, name = "message") String message, Principal principal) throws IOException {
        if(getId(principal).equals(senderId)){
            Message m = new Message(message,LocalDateTime.now(),senderId,receiverId);
            if(image != null) m.setImage(ImageCompress.compressBytes(image.getBytes()));

            return conversationDbService.sendMessage(senderId,receiverId,m,getAccessToken(principal));
        }

        throw new IllegalStateException("Access denied");
    }

    @PostMapping(value = "sendMessage/{senderId}/{receiverId}")
    @ResponseBody
    @RolesAllowed("user")
    public Conversation sendImage(@PathVariable("senderId") String senderId, @PathVariable("receiverId") String receiverId, @RequestBody(required = false) MultipartFile image,Principal principal) throws IOException {
        if(getId(principal).equals(senderId)){
            Message m = new Message(LocalDateTime.now(),senderId,receiverId);
            if(image != null) m.setImage(ImageCompress.compressBytes(image.getBytes()));

            return conversationDbService.sendMessage(senderId,receiverId,m,getAccessToken(principal));
        }

        throw new IllegalStateException("Access denied");

    }

    @GetMapping(value = "getmessages/{p1Id}/{p2Id}")
    @ResponseBody
    @RolesAllowed("user")
    public Conversation getMessages(@PathVariable("p1Id") String p1,@PathVariable("p2Id") String p2, Principal principal){
        if(getId(principal).equals(p1)){
            return conversationDbService.getConversation(p1,p2);
        }

        throw new IllegalStateException("Access denied");

    }

    private String getAccessToken(Principal principal) {
        KeycloakAuthenticationToken keycloakAuthenticationToken = (KeycloakAuthenticationToken) principal;
        String accessToken = keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getTokenString();
        return accessToken;
    }

    private String getId(Principal principal) {
        KeycloakAuthenticationToken keycloakAuthenticationToken = (KeycloakAuthenticationToken) principal;
        AccessToken accessToken = keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken();
        return (accessToken.getSubject());
    }

}
