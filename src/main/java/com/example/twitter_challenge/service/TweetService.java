package com.example.twitter_challenge.service;

import com.example.twitter_challenge.dto.TweetRequest;
import com.example.twitter_challenge.dto.TweetResponse;
import com.example.twitter_challenge.entity.Tweet;

import java.util.List;

public interface TweetService {

    TweetResponse create(TweetRequest request, String userName);

    List<TweetResponse> findByUserId(Long userId);

    TweetResponse findById(Long id);

    TweetResponse update(Long id, TweetRequest request, String userName);

    void delete(Long id, String userName);

    Tweet getTweetEntity(Long id);
}
