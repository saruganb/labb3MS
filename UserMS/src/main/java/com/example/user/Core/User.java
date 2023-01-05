package com.example.user.Core;

public class User {
    private String id;
    private String username;
    private String password;
    private int nrOfFriends;
    private int nrOfPost;
    private String about;


    public User(String id, String username, String password, int nrOfFriends, int nrOfPost, String about) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nrOfFriends = nrOfFriends;
        this.nrOfPost = nrOfPost;
        this.about = about;
    }

    /*public User(String username, String password, int nrOfFriends, int nrOfPost, String about) {
        this.username = username;
        this.password = password;
        this.nrOfFriends = nrOfFriends;
        this.nrOfPost = nrOfPost;
        this.about = about;
    }*/

    public User(String id, String username, int nrOfFriends, int nrOfPost, String about) {
        this.id = id;
        this.username = username;
        this.nrOfFriends = nrOfFriends;
        this.nrOfPost = nrOfPost;
        this.about = about;
    }

    public User(){}

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
