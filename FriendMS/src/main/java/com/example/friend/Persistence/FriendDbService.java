package com.example.friend.Persistence;

import com.example.friend.Core.Friend;
import com.example.friend.Core.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FriendDbService {
    private final FriendDbRepository friendDbRepository;
    @Autowired
    public FriendDbService(FriendDbRepository friendDbRepository){
        this.friendDbRepository = friendDbRepository;
    }

    @Transactional
    public void deleteFromTable(String friendId, String userId){
        friendDbRepository.deleteByFriendId(friendId, userId);
    }

    public User addFriend(String userId, String friendId,String accessToken){
        User user = getUser(userId,accessToken);
        if(user == null) throw new IllegalStateException("User with userId: " + userId + " doesnt exist");
        if(getUser(friendId,accessToken) == null) throw new IllegalStateException("User with userId: " + friendId + " doesnt exist");

        Optional<FriendDb> friendDb = friendDbRepository.isFriend(userId, friendId);
        if(friendDb.isPresent()){
            throw new IllegalStateException("Is already friend");
        }
        FriendDb f = new FriendDb(friendId,userId);
        friendDbRepository.save(f);

        incrementFriend(userId,accessToken);

        List<FriendDb> userDbFriends = friendDbRepository.getFriends(userId);
        System.out.println(userDbFriends);

        List<Friend> friends = new ArrayList<>();
        for (FriendDb userDbFriend : userDbFriends) {
            friends.add(new Friend(userDbFriend.getId(), userDbFriend.getFriendId(), userDbFriend.getUserId()));
        }
        System.out.println(friends);
        user.addListFriends(friends);

        //user.getFriends().add(new Friend(theFriendDb.getFriendId(), theFriendDb.getFriendId(), theFriendDb.getUserId()));
        //user.setNrOfFriends(user.getNrOfFriends() + 1);
        return user;


    }

    /*@Transactional
    public User addFriend(Long userId, Long friendId){
        Optional<UserDb> userDb = userDbRepository.findById(userId);
        if(userDb.isEmpty()) throw new IllegalStateException("User with " + userId + " doesnt exist");
        if(!userDb.get().addFriend(friendId, userId)) throw new IllegalArgumentException("Is already friend");
        userDb.get().setNrOfFriends(userDb.get().getNrOfFriends() + 1);
        userDbRepository.save(userDb.get());

        User user = new User(userDb.get().getId(), userDb.get().getUsername(), userDb.get().getNrOfFriends(), userDb.get().getNrOfPost(), userDb.get().getAbout());
        List<FriendDb> userDbFriends = userDb.get().getFriends();
        List<Friend> friends = new ArrayList<>();
        for (FriendDb userDbFriend : userDbFriends) {
            friends.add(new Friend(userDbFriend.getId(), userDbFriend.getFriendId(), userDbFriend.getUserId()));
        }
        user.addListFriends(friends);
        return user;
    }*/

    public List<User> getUserFriends(String userId,String accessToken){
        if(getUser(userId,accessToken) == null) throw new IllegalStateException("User with userId: " + userId + " doesnt exist");

        List<FriendDb> userDbfriends = friendDbRepository.getFriends(userId);
        List<User> theFriends = new ArrayList<>();
        for(int i = 0; i < userDbfriends.size(); i++){
            theFriends.add(getUser(userDbfriends.get(i).getFriendId(),accessToken));
        }

        return theFriends;
    }

    /*@Transactional
    public List<User> getUserFriends(Long userId){
        Optional<UserDb> userDb = userDbRepository.findById(userId);
        if(userDb.isEmpty()) throw new IllegalStateException("User with " + userId + " doesnt exist");
        List<FriendDb> userDbFriends = userDb.get().getFriends();
        if(userDbFriends == null) return null;

        User user = new User(userDb.get().getId(), userDb.get().getUsername(), userDb.get().getNrOfFriends(), userDb.get().getNrOfPost(), userDb.get().getAbout());
        List<Friend> friends = new ArrayList<>();
        List<User> theFriends = new ArrayList<>();
        for (FriendDb userDbFriend : userDbFriends) {
            friends.add(new Friend(userDbFriend.getId(), userDbFriend.getFriendId(), userDbFriend.getUserId()));
            theFriends.add(getFriend(userId, userDbFriend.getFriendId()));
        }
        user.addListFriends(friends);
        return theFriends;
    }*/

    public User getFriend(String userId, String friendId,String accessToken){
        User user = getUser(userId,accessToken);
        if(user == null) throw new IllegalStateException("User with userId: " + userId + " doesnt exist");
        User friend = getUser(friendId,accessToken);
        if(friend == null) throw new IllegalStateException("User with userId: " + friendId + " doesnt exist");


        Optional<FriendDb> friendDb = friendDbRepository.isFriend(userId, friendId);
        if(friendDb.isEmpty()){
            throw new IllegalStateException("Friend with friendId: " + friendId + " is not friend with User with userId: " + userId);
        }
        User theFriend = new User(friend.getId(), friend.getUsername(), friend.getNrOfFriends(), friend.getNrOfPost(), friend.getAbout());
        return theFriend;
    }

    /*@Transactional
    public User getFriend(Long userId, Long friendId){
        Optional<UserDb> userDb = userDbRepository.findById(userId);
        if(userDb.isEmpty()) return null;
        //throw new IllegalStateException("User with " + userId + " doesnt exist");
        System.out.println(userDb.get().getId());
        FriendDb friendDb = userDb.get().getFriend(friendId);
        if(friendDb == null) return null;
        //throw new IllegalStateException("Friend with " + friendId + " doesnt exist");
        System.out.println(friendDb.getUserId());
        Optional<UserDb> theFriendDb = userDbRepository.findById(friendId);
        if(theFriendDb.isEmpty()) return null;
        //throw new IllegalStateException("User with " + theFriendDb + " doesnt exist");
        System.out.println(theFriendDb.get().getUsername());
        User theFriend = new User(theFriendDb.get().getId(), theFriendDb.get().getUsername(), theFriendDb.get().getNrOfFriends(), theFriendDb.get().getNrOfPost(), theFriendDb.get().getAbout());
        System.out.println(theFriend);
        return theFriend;
    }*/

    public List<User> deleteFriend(String userId, String friendId, String accessToken){
        if(getUser(userId,accessToken) == null) throw new IllegalStateException("User with userId: " + userId + " doesnt exist");
        if(getUser(friendId,accessToken) == null) throw new IllegalStateException("User with userId: " + friendId + " doesnt exist");

        Optional<FriendDb> friendDb = friendDbRepository.isFriend(userId, friendId);
        if(friendDb.isEmpty()){
            throw new IllegalStateException("Friend with: " + friendId + " is not friend with User with userId: " + userId);
        }
        FriendDb f = new FriendDb(friendDb.get().getId(),friendDb.get().getFriendId(),friendDb.get().getUserId());
        friendDbRepository.delete(f);

        List<FriendDb> userDbFriends = friendDbRepository.getFriends(userId);
        System.out.println(userDbFriends);
        List<User> theFriends = new ArrayList<>();
        for (FriendDb userDbFriend : userDbFriends) {
            User u = getUser(userDbFriend.getFriendId(),accessToken);
            theFriends.add(u);
        }

        return theFriends;
    }


    private User getUser(String id, String accessToken){
        try{
            final String uri = "http://api1:8080/api/v1/user/get/" + id;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<User> response = restTemplate.exchange(uri, HttpMethod.GET, entity, User.class);
            System.out.println(response.getBody());
            User user = response.getBody();

            return user;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private void incrementFriend(String userId, String accessToken){
        try{
            final String uri = "http://api1:8080/api/v1/user/incrementFriend/" + userId;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    /*@Transactional
    public List<User> deleteFriend(Long userId, Long friendId){
        Optional<UserDb> userDb = userDbRepository.findById(userId);
        if(userDb.isEmpty()) throw new IllegalStateException("User with " + userId + " doesnt exist");
        try{
            userDb.get().deleteFriend(friendId);
        }catch(Exception e){e.printStackTrace();}

        userDb.get().setNrOfFriends(userDb.get().getNrOfFriends() - 1);
        userDbRepository.save(userDb.get());

        User user = new User(userDb.get().getId(), userDb.get().getUsername(), userDb.get().getNrOfFriends(), userDb.get().getNrOfPost(), userDb.get().getAbout());
        List<FriendDb> userDbFriends = userDb.get().getFriends();
        System.out.println(userDbFriends.size());
        List<Friend> friends = new ArrayList<>();
        List<User> theFriends = new ArrayList<>();
        for (FriendDb userDbFriend : userDbFriends) {
            friends.add(new Friend(userDbFriend.getId(), userDbFriend.getFriendId(), userDbFriend.getUserId()));
            theFriends.add(getFriend(userId, userDbFriend.getFriendId()));
        }
        user.addListFriends(friends);
        for (int i = 0; i<theFriends.size(); i++){
            System.out.println(theFriends.get(i));
        }

        return theFriends;
    }*/

}

