package com.example.user.Persistence;

import com.example.user.Core.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDbService {
    private final UserDbRepository userDbRepository;

    @Autowired
    public UserDbService(UserDbRepository userDbRepository) {
        this.userDbRepository = userDbRepository;
    }

    public String createUser(User user){
        Optional<UserDb> userDbs = userDbRepository.checkUnique(user.getUsername());
        if(userDbs.isPresent()){
            throw new IllegalStateException("Username is already taken");
        }
        return userDbRepository.save(new UserDb(user.getUsername(),user.getPassword(),0,0,user.getAbout())).getId();
    }

    public User login(User user){
        //Optional<UserDb> userDbs = userDbRepository.checkUserValid(user.getUsername(),user.getPassword());
        Optional<UserDb> userDbs = userDbRepository.findById(user.getId());

        if(userDbs.isEmpty()){
            userDbRepository.save(new UserDb(user.getId(),user.getUsername(),0,0,user.getAbout()));
            return new User(user.getId(),user.getUsername(),0,0,user.getAbout());
        }
        else {
            return new User(userDbs.get().getId(),userDbs.get().getUsername(),
                    userDbs.get().getNrOfFriends(),userDbs.get().getNrOfPost(),
                    userDbs.get().getAbout());
        }

        /*if(userDbs.isPresent()){
            return new User(userDbs.get().getId(),userDbs.get().getUsername(),
                    userDbs.get().getNrOfFriends(),userDbs.get().getNrOfPost(),
                    userDbs.get().getAbout());
        }*/
        //else throw new IllegalStateException("Wrong username or password");
    }

    public User updateBio(String id, String bio){
        Optional<UserDb> userDb = userDbRepository.findById(id);

        if(userDb.isEmpty()){
            throw new IllegalStateException("User with " + id + " doesnt exist");
        }
        userDb.get().setAbout(bio);
        userDbRepository.save(userDb.get());

        return new User(userDb.get().getId(),
                userDb.get().getUsername(),
                userDb.get().getNrOfFriends(),
                userDb.get().getNrOfPost(),
                userDb.get().getAbout());

    }
    public User getUserById(String id){
        Optional<UserDb> userDb = userDbRepository.findById(id);

        if(userDb.isEmpty()){
            throw new IllegalStateException("User with " + id + " doesnt exist");
        }
        return new User(userDb.get().getId(),
                userDb.get().getUsername(),
                userDb.get().getNrOfFriends(),
                userDb.get().getNrOfPost(),
                userDb.get().getAbout());
    }

    public User getUserByName(String username){
        Optional<UserDb> userDbs = userDbRepository.getUser(username);
        if(userDbs.isPresent()){
            return new User(userDbs.get().getId(),userDbs.get().getUsername(),
                    userDbs.get().getNrOfFriends(),userDbs.get().getNrOfPost(),
                    userDbs.get().getAbout());
        }
        else throw new IllegalStateException("Wrong username");
    }

    public void incrementPost(String id){
        Optional<UserDb> userDb = userDbRepository.findById(id);
        if(userDb.isEmpty()){
            throw new IllegalStateException("User with " + id + " doesnt exist");
        }

        userDb.get().setNrOfPost(userDb.get().getNrOfPost() + 1);
        userDbRepository.save(userDb.get());
    }

    public void incrementFriends(String id){
        Optional<UserDb> userDb = userDbRepository.findById(id);
        if(userDb.isEmpty()){
            throw new IllegalStateException("User with " + id + " doesnt exist");
        }

        userDb.get().setNrOfFriends(userDb.get().getNrOfFriends() + 1);
        userDbRepository.save(userDb.get());
    }
}
