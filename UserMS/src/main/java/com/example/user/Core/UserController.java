package com.example.user.Core;

import com.example.user.Persistence.UserDbService;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;

@CrossOrigin
@RestController
@RequestMapping(path = "api/v1/user")
public class UserController {

    private final UserDbService userDbService;

    @Autowired
    public UserController(UserDbService userDbService) {
        this.userDbService = userDbService;
    }

    @PostMapping(value ="create")
    @ResponseBody
    @RolesAllowed("user")
    public String createUser(@RequestBody User user){
        return userDbService.createUser(user);
    }

    @PostMapping(value = "login")
    @ResponseBody
    @RolesAllowed("user")
    public User login(@RequestBody User user, Principal principal){
        if(getId(principal).equals(user.getId())){
            return userDbService.login(user);
        }

        throw new IllegalStateException("Access denied");

    }

    @PutMapping(value = "bio/{id}/{bio}")
    @RolesAllowed("user")
    public User updateBio(@PathVariable("id") String id, @PathVariable("bio") String bio, Principal principal){
        if(getId(principal).equals(id)){
            return userDbService.updateBio(id,bio);
        }
        throw new IllegalStateException("Access denied");
    }
    @GetMapping(value = "get/{id}")
    @RolesAllowed("user")
    public User getUserInfo(@PathVariable("id") String id){
        return userDbService.getUserById(id);
    }

    @GetMapping(value = "getUser/{username}")
    @RolesAllowed("user")
    public User getUser(@PathVariable("username") String username) {
        return userDbService.getUserByName(username);
    }

    @PutMapping(value = "incrementPost/{id}")
    @RolesAllowed("user")
    public void incrementPost(@PathVariable("id") String id, Principal principal){
        if(getId(principal).equals(id)){
            userDbService.incrementPost(id);
        }

        throw new IllegalStateException("Access denied");

    }

    @PutMapping(value = "incrementFriend/{id}")
    @RolesAllowed("user")
    public void incrementFriends(@PathVariable("id") String id, Principal principal){
        if(getId(principal).equals(id)){
            userDbService.incrementFriends(id);
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
