package com.example.twitter_challenge.service;

import com.example.twitter_challenge.dto.RetweetResponse;

public interface RetweetService {

    RetweetResponse retweet(Long tweetId, String userName);

    void delete(Long retweetId, String userName);
}
