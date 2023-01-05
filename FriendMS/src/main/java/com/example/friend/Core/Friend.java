package com.example.friend.Core;

public class Friend {
    private Long Id;
    private String friendId;
    private String userId;

    public Friend(Long id, String friendId, String userId){
        this.Id = id;
        this.friendId = friendId;
        this.userId = userId;
    }

    public Friend(String friendId, String userId){
        this.friendId = friendId;
        this.userId = userId;
    }

    public Friend() {}

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
        return "Friend{" +
                "Id=" + Id +
                ", friendId=" + friendId +
                ", userId=" + userId +
                '}';
    }
}
