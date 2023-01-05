package com.example.user.Persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDbRepository  extends JpaRepository<UserDb,String> {
    @Query(value = "SELECT u FROM UserDb u WHERE u.username = ?1")
    Optional<UserDb> checkUnique(String username);

    @Query(value = "SELECT u FROM UserDb u where u.username = ?1 AND u.password = ?2")
    Optional<UserDb> checkUserValid(String username, String password);

    @Query(value = "SELECT u FROM UserDb u where u.username = ?1")
    Optional<UserDb> getUser(String username);
}
