package com.example.friend.Persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendDbRepository extends JpaRepository<FriendDb, Long> {
    @Modifying
    @Query(value = "delete from friendDb f where f.friendId = ?1 and f.userId = ?2")
    void deleteByFriendId(String friendId, String userId);

    @Query(value = "select f from friendDb f where f.userId = ?1 and f.friendId = ?2")
    Optional<FriendDb> isFriend(String userId, String friendId);

    @Query(value = "select f from friendDb f where f.userId = ?1")
    List<FriendDb> getFriends(String userId);

    @Query(value = "insert (friendId, userId) into friendDb f where f.userId = ?1 and f.friendId = ?2", nativeQuery = true)
    void addFriend(String userId, String friendId);
}
