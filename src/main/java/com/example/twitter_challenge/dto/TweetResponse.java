package com.example.twitter_challenge.dto;

import java.time.LocalDateTime;

public record TweetResponse(
        Long id,
        String content,
        Long userId,
        String userName,
        int likeCount,
        int commentCount,
        int retweetCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
