package com.example.twitter_challenge.service;

import com.example.twitter_challenge.dto.CommentRequest;
import com.example.twitter_challenge.dto.CommentResponse;
import com.example.twitter_challenge.dto.CommentUpdateRequest;

public interface CommentService {

    CommentResponse create(CommentRequest request, String userName);

    CommentResponse update(Long id, CommentUpdateRequest request, String userName);

    void delete(Long id, String userName);
}
