package com.example.post.Core;

import com.example.post.Persistence.PostDbService;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(path = "api/v1/")
public class PostController {
    private final PostDbService postDbService;

    @Autowired
    public PostController(PostDbService postDbService){
        this.postDbService = postDbService;
    }

    @GetMapping(value = "getUserFriendsPosts/{userId}")
    @ResponseBody
    @RolesAllowed("user")
    public List<User> getUserFriendsPosts(@PathVariable("userId") String userId, Principal principal){
        if(getId(principal).equals(userId)){
            return postDbService.getUserFriendsPosts(userId,getAccessToken(principal));
        }

        throw new IllegalStateException("Access denied");

    }

    @PostMapping(value = "post/{id}")
    @ResponseBody
    @RolesAllowed("user")
    public User addPost(@RequestBody Post post, @PathVariable("id") String userId, Principal principal){
        if(getId(principal).equals(userId)){
            post.setDate(LocalDate.now());
            return postDbService.addPost(post,userId,getAccessToken(principal));
        }

        throw new IllegalStateException("Access denied");
    }

    @GetMapping(value = "get/all/post/{id}")
    @ResponseBody
    @RolesAllowed("user")
    public List<Post> getPost(@PathVariable("id") String userId, Principal principal){
        return postDbService.getPost(userId,getAccessToken(principal));
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
