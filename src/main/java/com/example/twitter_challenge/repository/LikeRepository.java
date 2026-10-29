package com.example.twitter_challenge.repository;

import com.example.twitter_challenge.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, Long> {

    Optional<Likes> findByUserIdAndTweetId(Long userId, Long tweetId);

    boolean existsByUserIdAndTweetId(Long userId, Long tweetId);
}
