package com.example.twitter_challenge.service;

import com.example.twitter_challenge.entity.Tweet;
import com.example.twitter_challenge.entity.User;

import java.time.LocalDateTime;

/** Testlerde ortak kullanılan örnek kullanıcı ve tweet nesneleri. */
final class TestData {

    private TestData() {
    }

    static User user(Long id, String userName) {
        User user = new User(userName, userName + "@test.com", "encoded-password");
        user.setId(id);
        return user;
    }

    static Tweet tweet(Long id, String content, User owner) {
        Tweet tweet = new Tweet(content, owner);
        tweet.setId(id);
        tweet.setCreatedAt(LocalDateTime.now());
        return tweet;
    }
}
