package com.example.friend.Core;

import com.example.friend.Persistence.FriendDbService;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.context.annotation.Role;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(path = "api/v1")
public class FriendController {
    private final FriendDbService friendDbService;

    public FriendController(FriendDbService friendDbService){
        this.friendDbService = friendDbService;
    }

    @PostMapping(value = "addFriend/{userId}/{friendId}")
    @ResponseBody
    @RolesAllowed("user")
    public User addFriend(@PathVariable("userId") String userId, @PathVariable("friendId") String friendId, Principal principal){
        if (getId(principal).equals(userId)) {
            return friendDbService.addFriend(userId, friendId,getAccessToken(principal));
        }

        throw new IllegalStateException("Access denied");

    }

    @GetMapping(value = "getUserFriends/{userId}")
    @ResponseBody
    @RolesAllowed("user")
    public List<User> getFriends(@PathVariable("userId") String userId, Principal principal){
        if(getId(principal).equals(userId)){
            return friendDbService.getUserFriends(userId,getAccessToken(principal));
        }

        throw new IllegalStateException("Access denied");

    }

    @DeleteMapping(value = "deleteFriend/{userId}/{friendId}")
    @ResponseBody
    @RolesAllowed("user")
    public List<User> deleteFriend(@PathVariable("userId") String userId, @PathVariable("friendId") String friendId, Principal principal){
        //friendDbService.deleteFromTable(friendId, userId);
        if(getId(principal).equals(userId)){
            return friendDbService.deleteFriend(userId, friendId,getAccessToken(principal));
        }

        throw new IllegalStateException("Access denied");
    }

    @GetMapping(value = "getFriend/{userId}/{friendId}")
    @ResponseBody
    @RolesAllowed("user")
    public User getFriend(@PathVariable("userId") String userId, @PathVariable("friendId") String friendId, Principal principal){
        if(getId(principal).equals(userId)){
            return friendDbService.getFriend(userId, friendId,getAccessToken(principal));
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
