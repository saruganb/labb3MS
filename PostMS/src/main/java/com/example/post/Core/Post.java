package com.example.post.Core;

import java.time.LocalDate;

public class Post implements Comparable<Post>{
    private Long postId;
    private LocalDate date;
    private String post;
    private String userId;

    public Post(Long postId, LocalDate date, String post, String userId) {
        this.postId = postId;
        this.date = date;
        this.post = post;
        this.userId =userId;
    }

    public Post(LocalDate date, String post, String userId) {
        this.date = date;
        this.post = post;
        this.userId = userId;
    }

    public Post(){}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public Long getPostId() {
        return postId;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getPost() {
        return post;
    }

    @Override
    public int compareTo(Post p) {
        if(p.date.compareTo(date) > 0) return 1;
        else if(p.date.compareTo(date) == 0) return 0;
        else{
            return -1;
        }
    }

    @Override
    public String toString() {
        return "Post{" +
                "postId=" + postId +
                ", date=" + date +
                ", post='" + post + '\'' +
                ", userId=" + userId +
                '}';
    }
}