package com.example.post.Persistence;

import com.example.post.Core.Post;
import com.example.post.Core.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class PostDbService {
    private final PostDbRepository postDbRepository;

    @Autowired
    public PostDbService(PostDbRepository postDbRepository) {
        this.postDbRepository = postDbRepository;
    }

    public List<Post> getPost(String userId, String accessToken){
        if(getUser(userId,accessToken) == null) throw new IllegalStateException("User with userId: " + userId + " doesnt exist");

        List<PostDb> postDbs = postDbRepository.getPosts(userId);
        List<Post> posts = new ArrayList<>();
        for (int i = postDbs.size()-1; i >= 0; i--){
            posts.add(new Post(postDbs.get(i).getPostId(),
                    postDbs.get(i).getDate(),
                    postDbs.get(i).getPost(),
                    postDbs.get(i).getUserId()));
        }

        return posts;
    }
    /*public List<Post> getPost(Long id){
        Optional<UserDb> userDb = userDbRepository.findById(id);

        if(userDb.isEmpty()){
            throw new IllegalStateException("User with " + id + " doesnt exist");
        }
        List<PostDb> postDbs = userDb.get().getPostDbs();
        List<Post> posts = new ArrayList<>();
        for (int i = postDbs.size()-1; i >= 0; i--){
            posts.add(new Post(postDbs.get(i).getPostId(),
                    postDbs.get(i).getDate(),
                    postDbs.get(i).getPost()));
        }

        return posts;
    }*/

    public User addPost(Post post, String userId, String accessToken){

        if(getUser(userId,accessToken) == null) throw new IllegalStateException("User with userId: " + userId + " doesnt exist");

        PostDb postDb = new PostDb(post.getDate(),post.getPost(),userId);
        postDbRepository.save(postDb);

        incrementPost(userId,accessToken);

        User user = getUser(userId,accessToken);

        List<PostDb> postDbs = postDbRepository.getPosts(userId);
        List<Post> posts = new ArrayList<>();
        for (int i = postDbs.size()-1; i >= 0; i--){
            posts.add(new Post(postDbs.get(i).getPostId(),
                    postDbs.get(i).getDate(),
                    postDbs.get(i).getPost(),
                    postDbs.get(i).getUserId()));
        }
        user.addPost(posts);

        return user;
    }
    /*@Transactional
    public User addPost(Post post, Long id){
        Optional<UserDb> userDb = userDbRepository.findById(id);

        if(userDb.isEmpty()){
            throw new IllegalStateException("User with " + id + " doesnt exist");
        }
        PostDb postDb = new PostDb(post.getDate(),post.getPost());
        userDb.get().getPostDbs().add(postDb);
        userDb.get().setNrOfPost(userDb.get().getNrOfPost() + 1);
        userDbRepository.save(userDb.get());

        User user = new User(userDb.get().getId(),userDb.get().getUsername(),userDb.get().getNrOfFriends(),userDb.get().getNrOfPost(),userDb.get().getAbout());
        List<PostDb> postDbs = userDb.get().getPostDbs();
        List<Post> posts = new ArrayList<>();
        for (int i = postDbs.size()-1; i >= 0; i--){
            posts.add(new Post(postDbs.get(i).getPostId(),
                    postDbs.get(i).getDate(),
                    postDbs.get(i).getPost()));
        }
        user.addPost(posts);

        return user;

    }*/


    public List<User> getUserFriendsPosts(String userId, String accessToken){

        if(getUser(userId,accessToken) == null) throw new IllegalStateException("User with userId: " + userId + " doesnt exist");

        /*ResponseEntity<User[]> response =
                restTemplate.getForEntity(
                        "http://localhost:8080/employees/",
                        User[].class);
        User[] users = response.getBody();*/

        List<User> theFriends = getUserFriends(userId,accessToken);
        if(theFriends == null) throw new IllegalStateException("No Friends");

        List<PostDb> userPosts = new ArrayList<>();
        for(int i = 0; i < theFriends.size(); i++){
            List<PostDb> postDbs = postDbRepository.getPosts(theFriends.get(i).getId());
            for (int j = 0; j < postDbs.size(); j++){
                theFriends.get(i).addPost(new Post(postDbs.get(j).getPostId(), postDbs.get(j).getDate(), postDbs.get(j).getPost(), postDbs.get(j).getUserId()));
            }
            userPosts.addAll(postDbRepository.getPosts(theFriends.get(i).getId()));
        }
        Collections.sort(userPosts);

        List<User> theUsers = new ArrayList<>();
        for(int i = 0; i < userPosts.size(); i++){
            for(int j = 0; j < theFriends.size(); j++){
                for (int k = 0; k < theFriends.get(j).getPosts().size(); k++){
                    if(theFriends.get(j).getPosts().get(k).getPostId() == userPosts.get(i).getPostId()){
                        User u = new User(theFriends.get(j).getId(), theFriends.get(j).getUsername(),
                                theFriends.get(j).getNrOfFriends(), theFriends.get(j).getNrOfPost(), theFriends.get(j).getAbout());
                        u.addPost(new Post(userPosts.get(i).getPostId(), userPosts.get(i).getDate(), userPosts.get(i).getPost(), userPosts.get(i).getUserId()));
                        theUsers.add(u);
                        break;
                    }
                }
            }
        }
        return theUsers;
    }

    private List<User> getUserFriends(String id, String accessToken){
        try{
            final String uri  = "http://api3:8080/api/v1/getUserFriends/" + id;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<List<User>> response = restTemplate.exchange(uri, HttpMethod.GET, entity, new ParameterizedTypeReference<List<User>>() {});

            System.out.println(response.getBody());
            return response.getBody();


        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
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

    private void incrementPost(String userId, String accessToken){
        try{
            final String uri = "http://api1:8080/api/v1/user/incrementPost/" + userId;

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
    public List<User> getUserFriendsPosts(Long userId){
        Optional<UserDb> userDb = userDbRepository.findById(userId);
        if(userDb.isEmpty()) throw new IllegalStateException("User with " + userId + " doesnt exist");
        List<FriendDb> userDbFriends = userDb.get().getFriends();
        if(userDbFriends == null) return null;
        List<PostDb> userPosts = new ArrayList<>();
        List<UserDb> theFriendDbs = new ArrayList<>();
        for(int i = 0; i < userDbFriends.size(); i++){
            Optional<UserDb> friendDb = userDbRepository.findById(userDbFriends.get(i).getFriendId());
            if(friendDb.isEmpty()) throw new IllegalStateException("");
            theFriendDbs.add(friendDb.get());
        }

        for(int i = 0; i < userDbFriends.size(); i++){
            for(int j = 0; j < theFriendDbs.get(i).getPostDbs().size(); j++){
                userPosts.add(theFriendDbs.get(i).getPostDbs().get(j));
            }
        }
        Collections.sort(userPosts);


        List<User> users = new ArrayList<>();
        for(int i = 0; i < userPosts.size(); i++){
            for(int j = 0; j < theFriendDbs.size(); j++){
                for (int k = 0; k < theFriendDbs.get(j).getPostDbs().size(); k++){
                    if(theFriendDbs.get(j).getPostDbs().get(k).getPostId() == userPosts.get(i).getPostId()){
                        User u = new User(theFriendDbs.get(j).getId(), theFriendDbs.get(j).getUsername(),
                                theFriendDbs.get(j).getNrOfFriends(), theFriendDbs.get(j).getNrOfPost(), theFriendDbs.get(j).getAbout());
                        u.addPost(new Post(userPosts.get(i).getPostId(), userPosts.get(i).getDate(), userPosts.get(i).getPost()));
                        users.add(u);
                        break;
                    }
                }
            }
        }
        return users;


    }*/
}
