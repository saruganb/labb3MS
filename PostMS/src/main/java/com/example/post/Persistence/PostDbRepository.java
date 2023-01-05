package com.example.post.Persistence;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PostDbRepository extends JpaRepository<PostDb,Long> {

    @Query(value = "select p from postDb p where p.userId = ?1")
    List<PostDb> getPosts(String userId);

    @Query(value = "insert (date, post, userId) into postDb p where p.date = ?1 and p.post = ?2 and p.userId = ?3", nativeQuery = true)
    void addPost(LocalDate date, String post, String userId);

}
