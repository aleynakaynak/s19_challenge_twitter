package com.example.twitter_challenge.dto;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        Long tweetId,
        Long userId,
        String userName,
        LocalDateTime createdAt
) {
}
