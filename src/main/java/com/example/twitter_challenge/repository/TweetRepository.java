package com.example.twitter_challenge.repository;

import com.example.twitter_challenge.entity.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TweetRepository extends JpaRepository<Tweet, Long> {

    // Kullanıcının tweetlerini en yeniden eskiye sıralar
    List<Tweet> findByUserIdOrderByCreatedAtDesc(Long userId);
}
