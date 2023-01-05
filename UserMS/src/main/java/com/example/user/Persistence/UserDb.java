package com.example.user.Persistence;

import javax.persistence.*;

@Entity(name = "UserDb")
@Table
public class UserDb {
    @Id
    //@SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    @Column(name ="id", updatable = false, length = 400)
    private String id;

    @Column(name ="username", length = 60 , nullable = false, unique = true)
    private String username;

    @Column(name ="password", nullable = true, columnDefinition = "TEXT")
    private String password;

    @Column(name ="nrOfFriends", nullable = false)
    private int nrOfFriends;

    @Column(name ="nrOfPost", nullable = false)
    private int nrOfPost;

    @Column(name ="about", columnDefinition = "TEXT")
    private String about;

    public UserDb(String id, String username, String password, int nrOfFriends, int nrOfPost, String about) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nrOfFriends = nrOfFriends;
        this.nrOfPost = nrOfPost;
        this.about = about;
    }

    public UserDb(String id, String username, int nrOfFriends, int nrOfPost, String about) {
        this.id = id;
        this.username = username;
        this.nrOfFriends = nrOfFriends;
        this.nrOfPost = nrOfPost;
        this.about = about;
    }

    public UserDb() {

    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getNrOfFriends() {
        return nrOfFriends;
    }

    public int getNrOfPost() {
        return nrOfPost;
    }

    public String getAbout() {
        return about;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNrOfFriends(int nrOfFriends) {
        this.nrOfFriends = nrOfFriends;
    }

    public void setNrOfPost(int nrOfPost) {
        this.nrOfPost = nrOfPost;
    }

    public void setAbout(String about) {
        this.about = about;
    }


}
