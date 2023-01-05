package com.example.friend.Persistence;

import javax.persistence.*;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity(name = "friendDb")
@Table
public class FriendDb {

    @Id
    @SequenceGenerator(name="friend_sequence", sequenceName = "friend_sequence", allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE, generator = "friend_sequence")
    @Column(name = "Id", updatable = false)
    private Long Id;
    @Column(name = "friendId", updatable = false, nullable = false, columnDefinition = "TEXT")
    private String friendId;
    @Column(name = "userId", updatable = false, nullable = false, columnDefinition = "TEXT")
    private String userId;

    public FriendDb(Long id, String friendId, String userId){
        this.Id = id;
        this.friendId = friendId;
        this.userId = userId;
    }

    public FriendDb(String friendId, String userId){
        this.friendId = friendId;
        this.userId = userId;
    }

    public FriendDb() {}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    @Override
    public String toString() {
        return "FriendDb{" +
                "Id=" + Id +
                ", friendId=" + friendId +
                '}';
    }
}
