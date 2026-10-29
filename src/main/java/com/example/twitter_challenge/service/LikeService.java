package com.example.twitter_challenge.service;

import com.example.twitter_challenge.dto.LikeResponse;

public interface LikeService {

    LikeResponse like(Long tweetId, String userName);

    LikeResponse dislike(Long tweetId, String userName);
}
