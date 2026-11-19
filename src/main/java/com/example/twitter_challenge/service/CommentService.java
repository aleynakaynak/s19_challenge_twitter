package com.example.twitter_challenge.service;

import com.example.twitter_challenge.dto.CommentRequest;
import com.example.twitter_challenge.dto.CommentResponse;

public interface CommentService {

    CommentResponse create(Long tweetId, CommentRequest request, String userName);

    CommentResponse update(Long id, CommentRequest request, String userName);

    void delete(Long id, String userName);
}
