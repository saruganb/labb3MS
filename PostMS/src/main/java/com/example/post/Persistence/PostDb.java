package com.example.post.Persistence;

import javax.persistence.*;
import java.time.LocalDate;

@Entity(name="postDb")
@Table
public class PostDb implements Comparable<PostDb> {
    @Id
    @SequenceGenerator(name = "post_sequence", sequenceName = "post_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_sequence")
    @Column(name ="postId", updatable = false)
    private Long postId;

    @Column(name ="date", nullable = false, updatable = false)
    private LocalDate date;

    @Column(name ="post", nullable = false, updatable = false)
    private String post;

    @Column(name = "userId", nullable = false, updatable = false)
    private String userId;

    public PostDb(Long postId, LocalDate date, String post, String userId) {
        this.postId = postId;
        this.date = date;
        this.post = post;
        this.userId = userId;
    }

    public PostDb(LocalDate date, String post, String userId) {
        this.date = date;
        this.post = post;
        this.userId = userId;
    }

    public PostDb() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setPost(String post) {
        this.post = post;
    }

    @Override
    public String toString() {
        return "PostDb{" +
                "postId=" + postId +
                ", date=" + date +
                ", post='" + post + '\'' +
                ", userId=" + userId +
                '}';
    }

    @Override
    public int compareTo(PostDb p) {
        if(p.date.compareTo(date) > 0) return 1;
        else if(p.date.compareTo(date) == 0) return 0;
        else{
            return -1;
        }
    }
}
